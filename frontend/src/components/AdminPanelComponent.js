import React, {useState, useEffect} from "react";
import Table from 'react-bootstrap/Table';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import {Link} from "react-router-dom";
import {getHeaders} from "../services/AuthHeader";
import axios from "axios";


const AdminPanelComponent = () => {


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
    }, [labs]);

    const getUsers = () => {
        labs.forEach(lab => {
            axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/lab-users/${lab.labName}`, getHeaders())
                .then((response) => {
                    setAssignedUsers((prevAssignedUsers) => ({
                        ...prevAssignedUsers,
                        [lab.labName]: response.data.join('\n')
                    }));
                }).catch((error) => {
                setAssignedUsers("Error");
            });
        })
    };

    const getTeams = () => {
        labs.forEach(lab => {
            axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/lab-teams/${lab.labName}`, getHeaders())
                .then((response) => {
                    setAssignedTeams((prevAssignedTeams) => ({
                        ...prevAssignedTeams,
                        [lab.labName]: response.data.join('\n')
                    }));
                }).catch((error) => {
                setAssignedTeams("Error");
            });
        })
    };

    const getAllLabs = () => {
        axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/labs/`, getHeaders())
            .then((response) => {
                setLabs(response.data);
                console.log(response.data);
            }).catch((error) => {
            console.log(error);
        })
    };

    const deleteLab = (labName) => {
        if (window.confirm("Are you sure you want to delete this lab?")) {
            axios.delete(`${process.env.REACT_APP_SPRING_HOST}/v1/labs/` + labName, getHeaders())
                .then((response) => {
                    console.log(response);
                    window.location.reload();
                }).catch((error) => {
                console.log(error);
            })
        }
    }

    return (
        <div>
            <h1 className="text-center">Admin Panel</h1>
            <div className="btn-group mb-3" role="group" aria-label="Assign, add buttons">
                <Link to="/panel/assign-users" className="btn btn-success mr-2">Assign Users</Link>
                <Link to="/panel/assign-teams" className="btn btn-danger mr-2">Assign Teams</Link>
                <Link to="/add-lab" className="btn btn-primary">Add Lab</Link>
            </div>
            <div className="d-flex flex-wrap justify-content-center">
                <div className="btn-group mr-2 mb-3" role="group" aria-label="Users, teams buttons">
                    <Link to="/users" className="btn btn-info">Users Panel</Link>
                    <Link to="/teams" className="btn btn-warning">Teams Panel</Link>
                </div>
            </div>


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
                        <td>Host</td>
                        <td>Users/Teams</td>
                        <td>Actions</td>

                    </tr>
                    </thead>
                    <tbody>

                    {labs
                        .filter((labs) => {
                            return search.toLowerCase() === ''
                                ? labs
                                : labs.labName.toLowerCase().includes(search) ||
                                labs.userName.toLowerCase().includes(search) ||
                                labs.host.toLowerCase().includes(search) ||
                                assignedUsers[labs.labName].toLowerCase().includes(search) ||
                                assignedTeams[labs.labName].toLowerCase().includes(search);

                        }).map(
                            labs =>
                                <tr key={labs.labName}>
                                    <td>{labs.labName}</td>
                                    <td>{labs.userName}</td>
                                    <td>{labs.host}</td>
                                    <td>{assignedUsers[labs.labName]}
                                        {"\n"}
                                        {assignedTeams[labs.labName] ?
                                            <span style={{color: 'green'}}>{assignedTeams[labs.labName]}</span> : ''}
                                    </td>
                                    <td>
                                        <button onClick={() => deleteLab(labs.labName)}
                                                className="btn btn-danger">Delete
                                        </button>
                                        <Link className="btn btn-secondary" to={`/edit-lab/${labs.labName}`}>Edit</Link>
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
export default AdminPanelComponent;