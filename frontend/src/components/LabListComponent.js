import React, {useState, useEffect, version} from "react";
import Table from 'react-bootstrap/Table';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import {Link} from "react-router-dom";
import { checkAuthentication, getHeaders } from "../services/AuthHeader";
import axios from "axios";


const LabListComponent = () => {
    checkAuthentication();
    const [labs, setLabs] = useState([]);
    const [search, setSearch] = useState('');
    const [assignedUsers, setAssignedUsers] = useState({});
    const [assignedTeams, setAssignedTeams] = useState('');



    useEffect(() => {


        getAllLabs();
    }, []);

    useEffect(() => {
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
            }).catch((error) => {
            console.log(error);
        })
    };

    return(
        <div>
            <h1 className = "text-center"> Lab List</h1>


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
                        <td style={{fontSize: '20px'}}><b>Name</b></td>
                        <td style={{fontSize: '20px'}}><b>Username</b></td>
                        <td style={{fontSize: '20px'}}><b>Password</b></td>
                        <td style={{fontSize: '20px'}}><b>Host</b></td>
                        <td style={{fontSize: '20px'}}><b>Teams</b></td>
                        <td style={{fontSize: '20px'}}><b>Users</b></td>
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
                                assignedUsers[labs.labName].toLowerCase().includes(search) ||
                                assignedTeams[labs.labName].toLowerCase().includes(search);
                        }).map(
                            labs =>
                                <tr key={labs.labName}>
                                    <td>{labs.labName}</td>
                                    <td>{labs.userName}</td>
                                    <td style={{ color: labs.password === "hidden" ? "gray" : "black" }}>{labs.password}</td>
                                    <td>{labs.host}</td>
                                    <td>{<span style={{color: 'green'}}>{assignedTeams[labs.labName]}</span>}</td>
                                    <td>{assignedUsers[labs.labName]}</td>
                                </tr>
                        )
                    }

                    </tbody>
                </Table>
            </Container>
            <div>
                <h8 className="text-center">Total Labs: {labs.length}
                    <br/> Platform Labs: {labs.filter(lab => assignedTeams[lab.labName] && assignedTeams[lab.labName].includes('platform')).length}
                    <br/> Application Labs: {labs.filter(lab => assignedTeams[lab.labName] && assignedTeams[lab.labName].includes('application')).length}

                </h8>
                <h8 className={labs.length === 0 ? "text-center" : "d-none"}>No labs to display.</h8>
                <br/>Password is hidden for users who doesnt own the lab or admin.
            </div>
        </div>

    )
}


export default LabListComponent;
