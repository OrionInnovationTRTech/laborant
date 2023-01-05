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
        const toggleExpanded = () => setExpanded(!expanded);
        const loggedInUser = localStorage.getItem('username');



        useEffect(() => {


            getAllLabs();
        }, []);

        useEffect(() => {
            getLabVersion();
            getUsers();
    },[labs]);

        const getUsers = () => {
            labs.forEach(lab => {axios.get(`http://localhost:8080/v1/lab-users/${lab.labName}`, getHeaders())
            .then((response) => {
            // set the list of assigned users for the lab
            setAssignedUsers((prevAssignedUsers) => ({...prevAssignedUsers, [lab.labName]: response.data.join('\n')}));
            }).catch((error) => {
                setAssignedUsers("Error");
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


                            setLabVersion((prevLabVersions) => ({...prevLabVersions, [lab.labName]: labVersion}));
                                setIsMulti((prevIsMulti) => ({...prevIsMulti, [lab.labName]:isMulti}));
                        }).catch((error) => {
                            console.log(lab.labName + "couldnt connect")
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
                setTimeout(() => {
                    window.location.replace('/labs');
                  }, 50);
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
                    <h1 className = "text-center"> Lab List<Link to="/add-lab" className="btn btn-primary" style={{marginLeft: '700px'}}>Add Lab</Link></h1>

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
                                <td>Assigned User</td>
                                <td>Is Multi</td>
                                <td>Is Reserved</td>
                                <td>Actions <button onClick={toggleExpanded} className="btn btn-outline-success">
                                    {expanded ? '▼' : '▶'}
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
                                        labs.reserved.toLowerCase().includes(search) ||
                                        labVersion[labs.labName].toLowerCase().includes(search) ||
                                        assignedUsers[labs.labName].toLowerCase().includes(search) ||
                                        isMulti[labs.labName].toLowerCase().includes(search);

                                    }).map(
                                    labs =>
                                    <tr key={labs.labName}>
                                        <td>{labs.labName}</td>
                                        <td>{labs.userName}</td>
                                        <td style={{ color: labs.password === "hidden" ? "gray" : "black" }}>{labs.password}</td>
                                        <td>{labs.host}</td>
                                        <td>{labs.port}</td>
                                        <td>{labVersion[labs.labName]}</td>
                                        <td>{assignedUsers[labs.labName]}</td>
                                        <td>{isMulti[labs.labName]}</td>
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



                </div>

            )
        }


    export default LabListComponents;