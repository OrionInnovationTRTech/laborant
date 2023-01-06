    import React, {useState, useEffect, version} from "react";
    import Table from 'react-bootstrap/Table';
    import Container from 'react-bootstrap/Container';
    import Form from 'react-bootstrap/Form';
    import InputGroup from 'react-bootstrap/InputGroup';
    import {Link} from "react-router-dom";
    import { checkAuthentication, getHeaders } from "../services/AuthHeader";
    import axios from "axios";



    const LabListComponents = () => {
        checkAuthentication();


        const [labs, setLabs] = useState([]);
        const [labVersion, setLabVersion] = useState({});
        const [isMulti, setIsMulti] = useState({});
        const [search, setSearch] = useState('');
        const [assignedUsers, setAssignedUsers] = useState({});
        const [expanded, setExpanded] = useState(false);
        const [assignedTeams, setAssignedTeams] = useState('');
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
            labs.forEach(lab => {axios.get(`http://localhost:8080/v1/lab-users/${lab.labName}`, getHeaders())
            .then((response) => {
            setAssignedUsers((prevAssignedUsers) => ({...prevAssignedUsers, [lab.labName]: response.data.join('\n')}));
            }).catch((error) => {
                setAssignedUsers("Error");
            });
        })};

         const getTeams = () => {
            labs.forEach(lab => {axios.get(`http://localhost:8080/v1/lab-teams/${lab.labName}`, getHeaders())
            .then((response) => {
            setAssignedTeams((prevAssignedTeams) => ({...prevAssignedTeams, [lab.labName]: response.data.join('\n')}));
            }).catch((error) => {
                setAssignedTeams("Error");
            });
        })};


         const getAllLabs = () => {
            axios.get('http://localhost:8080/v1/labs/', getHeaders())
            .then((response) => {
                setLabs(response.data);
                console.log("labs: " + response.data);
            }).catch((error) => {
                console.log(error);
            })
        };

             const getLabVersion = () => {
                let labVersion = "CAN'T CONNECT";
                let isMulti = "";
                labs.forEach(lab => {
                    console.log("for: " +lab.labName)
                    axios.get(`http://localhost:8080/v1/labs/status/${lab.labName}`, getHeaders())
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
                        labVersion = "CAN'T CONNECT";
                        setLabVersion((prevLabVersions) => ({...prevLabVersions, [lab.labName]: labVersion}));
                        setIsMulti((prevIsMulti) => ({...prevIsMulti, [lab.labName]:isMulti}));
                    });

                });
            }



        const deleteLab = (labName) => {
    if (window.confirm("Are you sure you want to delete this lab?")) {
        axios.delete("http://localhost:8080/v1/labs/" + labName, getHeaders())
            .then((response) => {
                console.log(response);
                window.location.reload();
            }).catch((error) => {
                window.alert(error.response.data.message);
            })
    }
}

        const reserveLab = (labName) => {
            if (window.confirm("Are you sure you want to reserve this lab?")) {
                axios.put(`http://localhost:8080/v1/reserve-lab`, {}, {
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

        const unreserveLab = (labName) => {
            if (window.confirm("Are you sure you want to unreserve this lab?")) {
                axios.put(`http://localhost:8080/v1/unreserve-lab`, {}, {
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

            return(
                <div>
                    <h1 className = "text-center"> Lab List<Link to="/add-lab" className="btn btn-primary" style={{marginLeft: '600px'}}>Add Lab</Link><Link to="/bulk-add" className="btn btn-outline-primary">Bulk Add</Link></h1>


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
                                <td>Name</td>
                                <td>Username</td>
                                <td>Password</td>
                                <td>Host</td>
                                <td>Port</td>
                                <td>Version</td>
                                <td>Users/Teams</td>
                                <td>Is Reserved</td>
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
                                        labs.userName.toLowerCase().includes(search) ||
                                        labs.password.toLowerCase().includes(search) ||
                                        labs.host.toLowerCase().includes(search) ||
                                        labVersion[labs.labName].toLowerCase().includes(search) ||
                                        assignedUsers[labs.labName].toLowerCase().includes(search) ||
                                        assignedTeams[labs.labName].toLowerCase().includes(search);
                                    }).map(
                                    labs =>
                                    <tr key={labs.labName}>
                                        <td>{labs.labName}</td>
                                        <td>{labs.userName}</td>
                                        <td style={{ color: labs.password === "hidden" ? "gray" : "black" }}>{labs.password}</td>
                                        <td>{labs.host}</td>
                                        <td>{labs.port}</td>
                                        <td>{labVersion[labs.labName]}</td>
                                        <td>
                                            {assignedUsers[labs.labName]}
                                            {"\n"}
                                            {assignedTeams[labs.labName] ? <span style={{color: 'green'}}>{assignedTeams[labs.labName]}</span> : ''}
                                        </td>
                                        <td>{labs.reserved === true ? "TRUE"  : ""}
                                        </td>
                                        <td>
                                            <table>
                                                            {expanded && (
                                                                <>

                                                                    <button onClick={() => deleteLab(labs.labName)} className="btn btn-danger">Delete</button>
                                                                    <Link className="btn btn-secondary" to={`/edit-lab/${labs.labName}`}>Edit</Link>
                                                                    <Link className="btn btn-success" to={`/run-command/${labs.labName}`}>Run Status</Link>
                                                                    { labs.users === null || labs.users.length === 0 || labs.users.some(user => user.username === loggedInUser) ? (
                                                                        labs.reserved ? (
                                                                            <button onClick={() => unreserveLab(labs.labName)} className="btn btn-dark">Unreserve</button>
                                                                        ) : (
                                                                            <button onClick={() => reserveLab(labs.labName)} className="btn btn-warning">Reserve</button>
                                                                        )
                                                                    ) : null }
                                                                </>
                                                            )}
                                            </table>
                                        </td>
                                    </tr>
                                )
                            }

                        </tbody>
                        </Table>
                    </Container>
                    <div>
                        <h8 className="text-center">Total Labs: {labs.length} </h8>
                        <h8 className={labs.length === 0 ? "text-center" : "d-none"}>No labs to display.</h8>
                        <br />
                        <h8 className={labs.length === 0 ? "d-none" : "text-center"}>Click on the "Actions" button to see more options.</h8>
                        <br />
                        <h8 className="text-center">The lab versions which has '**' indicates that it is a multi lab.</h8>
                        <br/>
                        <body className="text-center">Green text on Users/Teams indicates that it is a team, not user. Black text is shows that owner is user. <br/>Password is hidden for users who doesnt own the lab or admin.
                        Reservation can only be made by lab owner if it is assigned to single user. If it is a team lab everyone can reserve it.</body>


                 </div>
                </div>

            )
        }


    export default LabListComponents;
