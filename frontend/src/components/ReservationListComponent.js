    import React, {useState, useEffect, version} from "react";
    import Table from 'react-bootstrap/Table';
    import Container from 'react-bootstrap/Container';
    import Form from 'react-bootstrap/Form';
    import InputGroup from 'react-bootstrap/InputGroup';
    import {Link} from "react-router-dom";
    import { checkAuthentication, getHeaders } from "../services/AuthHeader";
    import axios from "axios";
    import moment from 'moment';
    import {Fragment} from "react";
    import Modal from 'react-bootstrap/Modal';
    import Button from "react-bootstrap/Button";



    const ReservationListComponent = () => {
        checkAuthentication();


        const [labs, setLabs] = useState([]);
        const [labVersion, setLabVersion] = useState({});
        const [isMulti, setIsMulti] = useState({});
        const [search, setSearch] = useState('');
        const [assignedUsers, setAssignedUsers] = useState({});
        const [expanded, setExpanded] = useState(false);
        const [nameSortOrder, setNameSortOrder] = useState('init');
        const [reservationSortOrder, setReservationSortOrder] = useState('init')
        const [assignedTeams, setAssignedTeams] = useState('');
        const [showModal, setShowModal] = useState(false);
        const [modalCommand, setModalCommand] = useState('');
        const [modalResponse, setModalResponse] = useState('');
        const [modalLabName, setModalLabName] = useState('');
        const [enteredCommand, setEnteredCommand] = useState('');


        const toggleExpanded = () => setExpanded(!expanded);
        const loggedInUser = localStorage.getItem('username');




        useEffect(() => {


            getAllLabs();
        }, []);

        useEffect(() => {
            getLabVersion();
            getUsers();
            getTeams();
    },[labs]);

         const getUsers = () => {
            labs.forEach(lab => {axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/lab-users/${lab.labName}`, getHeaders())
            .then((response) => {
            setAssignedUsers((prevAssignedUsers) => ({...prevAssignedUsers, [lab.labName]: response.data.join('\n')}));
            }).catch((error) => {
                setAssignedUsers("Error");
            });
        })};

         const getTeams = () => {
            labs.forEach(lab => {axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/lab-teams/${lab.labName}`, getHeaders())
            .then((response) => {
            setAssignedTeams((prevAssignedTeams) => ({...prevAssignedTeams, [lab.labName]: response.data.join('\n')}));
            }).catch((error) => {
                setAssignedTeams("Error");
            });
        })};


         const getAllLabs = () => {
            axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/labs/`, getHeaders())
            .then((response) => {
                setLabs(response.data);
                console.log("labs: " + response.data);
            }).catch((error) => {
                console.log(error);
            })
        };

             const getLabVersion = () => {
                let labVersion = "NC";
                let isMulti = "";
                labs.forEach(lab => {
                    console.log("for: " +lab.labName)
                    axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/labs/status/${lab.labName}`, getHeaders())
                        .then((response) => {
                            const data = response.data.split('\n').map(row => {
                                return row.split(' ').filter(element => element !== '');

                            }).filter(row => row.length > 0);

                                console.log(data);
                                labVersion = data[1][1];
                                console.log(lab.labName  + " " + labVersion);

                                if(data[2] !== undefined && data[2] !== null){
                                    isMulti = "TRUE";
                                }

                                setIsMulti((prevIsMulti) => ({...prevIsMulti, [lab.labName]:isMulti}));
                               if (isMulti === "TRUE"){
                                   setLabVersion((prevLabVersions) => ({
                                       ...prevLabVersions,
                                       [lab.labName]: `${labVersion}**`
                                   }))
                               }
                               else {
                                   setLabVersion((prevLabVersions) => ({
                                       ...prevLabVersions,
                                       [lab.labName]: labVersion
                                   }));
                               }
                        }).catch((error) => {
                            console.log(lab.labName + "could not connect")
                        console.log("for lab:" + lab.labName + "     " + error)
                        labVersion = "NC";
                        setLabVersion((prevLabVersions) => ({...prevLabVersions, [lab.labName]: labVersion}));
                        setIsMulti((prevIsMulti) => ({...prevIsMulti, [lab.labName]:isMulti}));
                    });

                });
            };

        const unreserveLab = (labName) => {
            if (window.confirm("Are you sure you want to unreserve this lab?")) {
                axios.put(`${process.env.REACT_APP_SPRING_HOST}/v1/unreserve-lab`, {}, {
                headers: {
                    'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
                },
                params:  {
                    labName: labName
                }
                })
                    .then((response) => {
                        console.log(response);
                        setTimeout(() => {
                            window.location.replace('/labs');
                          }, 50);
                    }).catch((error) => {
                        window.alert(error.response.data.message);
                    })
                }
        }

        const sortLabsByReservation = (a, b) => {
            if (a.reserved === b.reserved) {
                if (!a.reserved) {
                    return 0;
                } else {
                    return a.reservedUntil > b.reservedUntil ? 1 : -1;
                }
            } else {
                return a.reserved ? -1 : 1;
            }
        }

        const sortLabsByName = (a, b) => {
            return a.labName > b.labName ? 1 : -1;
        }

        const handleSortByName = () => {
            setReservationSortOrder('init');
            setNameSortOrder(nameSortOrder === 'ascending' ? 'descending' : 'ascending');
            setLabs([...labs].sort((a, b) => nameSortOrder === 'ascending' ? sortLabsByName(a, b) : sortLabsByName(b, a)));
        }

        const handleSortByReservation = () => {
            setNameSortOrder('init');
            setReservationSortOrder(reservationSortOrder === 'ascending' ? 'descending' : 'ascending');
            setLabs([...labs].sort((a, b) => reservationSortOrder === 'ascending' ? sortLabsByReservation(a, b) : sortLabsByReservation(b, a)));
        }

        const handleOpenModal = (labName) => {
            setShowModal(true);
            setModalLabName(labName);
        };

        const handleCloseModal = () => {
            setShowModal(false);
            setEnteredCommand('');
            setModalCommand('');
            setModalResponse('');
        };

        const handleRunCommand = (labName) => {
            axios.post(`${process.env.REACT_APP_SPRING_HOST}/v1/labs/command/`,{},{
                headers: {
                    'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
                },
                params: {
                    labName: labName,
                    command: modalCommand
                }
            })
                .then(response => {
                    setModalResponse(prevModalResponse => [...prevModalResponse, <React.Fragment>COMMAND: {modalCommand}<br />SERVER: {response.data}<br /><br/></React.Fragment>]);
                })
                .catch(error => {
                    setModalResponse(prevModalResponse => [...prevModalResponse, <React.Fragment>Error sending the command: {error}<br /></React.Fragment>]);
                });
        };





        return(
            <div>
                <h1 className = "text-center">Lab Reservation<Link to="/lab-list" className="btn btn-outline-primary" style={{marginLeft: '600px'}}>Lab List</Link></h1>


                <Container>
                    <Form>
                        <InputGroup className='my-3'>
                            <Form.Control
                                onChange={(e) => setSearch(e.target.value)}
                                placeholder='Filter labs...'
                            />
                        </InputGroup>
                    </Form>
                    <Table bordered hover>
                        <thead>
                        <tr>
                            <td style={{fontSize: '20px'}}><b><span onClick={handleSortByName}>Name</span></b>
                                {nameSortOrder === 'ascending' ? <span onClick={handleSortByName}> &#9650;</span> :nameSortOrder === 'descending' ? <span onClick={handleSortByName}>&#9660;</span>:null}</td>
                            <td style={{fontSize: '20px'}}><b>Host</b></td>
                            <td style={{fontSize: '20px'}}><b>Version</b></td>
                            <td style={{fontSize: '20px'}}><b>Owner</b></td>
                            <td style={{fontSize: '20px'}}>
                                <b>
                                    <span onClick={handleSortByReservation}>Reservation</span>
                                    {reservationSortOrder === 'ascending' ? <span onClick={handleSortByReservation}> &#9650;</span> :
                                        reservationSortOrder === 'descending' ? <span onClick={handleSortByReservation}>&#9660;</span> : null}
                                </b>

                            </td>
                            <td><button onClick={toggleExpanded} className="btn btn-outline-success">
                                {expanded ? 'Actions ▼' : 'Actions ▶'}
                            </button></td>

                        </tr>
                        </thead>
                        <tbody>

                        {labs
                            .filter((labs) => {
                                return search.toLowerCase() === ''
                                    ? labs
                                    : labs.labName.toLowerCase().includes(search) ||
                                    labs.host.toLowerCase().includes(search) ||
                                    labVersion[labs.labName].toLowerCase().includes(search) ||
                                    assignedUsers[labs.labName].toLowerCase().includes(search) ||
                                    assignedTeams[labs.labName].toLowerCase().includes(search) ||
                                    labs.reserved && labs.reserved.toString().includes(search);
                            }).map(
                                labs =>
                                    <tr key={labs.labName}>
                                        <td>{labs.labName}</td>
                                        <td>{labs.host}</td>
                                        <td>{labVersion[labs.labName]}</td>
                                        <td>
                                            {assignedUsers[labs.labName]}
                                            {assignedUsers[labs.labName] && assignedTeams[labs.labName] ?
                                                <span style={{color: 'darkviolet'}}> | </span> : null}
                                            {assignedTeams[labs.labName] ? <span style={{color: 'green'}}>{assignedTeams[labs.labName]}</span> : ''}
                                        </td>
                                        <td>
                                            {labs.reserved === true && labs.reservedBy !== null && labs.reservedUntil !== null ? (
                                                <Fragment>
                                                    <span>Reserved by</span>
                                                    <br/>
                                                    <span>{labs.reservedBy.username} until</span>
                                                    <br/>
                                                    <span>{moment(labs.reservedUntil).format('DD-MM-YY HH:mm')}</span>
                                                </Fragment>
                                            ) : (
                                                <span style={{ color: 'darkkhaki' }}>Available</span>
                                            )}
                                        </td>
                                        <td>
                                            <table>
                                                {expanded && (
                                                    <>
                                                        { labs.users === null || labs.users.length === 0 || labs.users.some(user => user.username === loggedInUser) ? (
                                                            labs.reserved ? (
                                                                <button onClick={() => unreserveLab(labs.labName)} className="btn btn-dark">Unreserve</button>
                                                            ) : (
                                                                <Link className="btn btn-outline-primary d-md-flex" to={`/reserve-lab/${labs.labName}`}>Reserve</Link>
                                                            )
                                                        ) : null }
                                                        <Link className="btn btn-success d-md-flex" to={`/run-command/${labs.labName}`}>Run Status</Link>
                                                        <Button onClick={() => handleOpenModal(labs.labName)}>Run Command</Button>
                                                    </>
                                                )}
                                            </table>
                                        </td>

                                    </tr>
                            )
                        }

                        </tbody>
                    </Table>
                    <Modal show={showModal} onHide={handleCloseModal}>
                        <Modal.Header closeButton>
                            <Modal.Title>Command Line</Modal.Title>
                        </Modal.Header>

                        <Modal.Body>

                            {modalResponse}
                            <br />
                            <br />
                            <Form.Control
                            as="textarea"
                            rows="1"
                            placeholder="Enter your command"
                            value={modalCommand}
                            onChange={e => setModalCommand(e.target.value)}
                        />
                        </Modal.Body>

                        <Modal.Footer>
                            <Button variant="secondary" onClick={handleCloseModal}>
                                Close
                            </Button>
                            <Button variant="primary" onClick={() => handleRunCommand(modalLabName)}>
                                Run Command
                            </Button>
                        </Modal.Footer>
                    </Modal>
                </Container>
                <div>
                </div>
            </div>
        )
    }




    export default ReservationListComponent;