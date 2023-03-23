import React, {useState, useEffect} from "react";
import {Link} from "react-router-dom";
import axios from "axios";
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Table from 'react-bootstrap/Table';
import {checkAuthentication, getHeaders} from "../services/AuthHeader";
const base = process.env.REACT_APP_BASE_PATH || '';


const UserListComponents = () => {
    checkAuthentication();
    const [users, setUsers] = useState([]);
    const [search, setSearch] = useState('');
    const [assignedLabs, setAssignedLabs] = useState({});

    useEffect(() => {
        getAllUsers();
    }, []);

    useEffect(() => {
        getLabs();
    }, [users]);

    const getAllUsers = () => {
         axios.get(`${process.env.REACT_APP_SPRING_HOST}/users/`, getHeaders())
          .then((response) => {
            setUsers(response.data);
            console.log(response.data);
        }).catch((error) => {
            console.log(error);
        })
    }

    const getLabs = () => {
        users.forEach(user => {axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/user-labs/${user.username}`, getHeaders())
            .then((response) => {
                setAssignedLabs((prevAssignedLabs) => ({...prevAssignedLabs, [user.username]: response.data.join('\n')}));
            }).catch((error) => {
                setAssignedLabs("Error");
            });
        })};


    const deleteUser = (username) => {
        if (window.confirm("Are you sure you want to delete this user?")) {
            axios.delete(`${process.env.REACT_APP_SPRING_HOST}/users/delete/` + username, getHeaders())
                .then((response) => {
                    console.log(response);
                    window.location.replace(base+'/users');
                }).catch((error) => {
                    console.log(error);
                })
        }
    }


        return(
            <div>
                <h1 className = "text-center">User List<Link to="/add-user" className="btn btn-primary" style={{marginLeft: '700px'}}>Add User</Link></h1>

                        <Container>
                        <Form>
                        <InputGroup className='my-3'>
                            <Form.Control
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder='Filter users...'
                            />
                        </InputGroup>
                        </Form>
                <Table hover>
                    <thead>
                        <tr>
                            <td>Username</td>
                            <td>Password</td>
                            <td>Role</td>
                            <td>Assigned Labs</td>
                            <td>Actions</td>
                        </tr>
                    </thead>
                    <tbody>
                    {users
                            .filter((users) => {
                                      return search.toLowerCase() === ''
                                        ? users
                                        : users.username.toLowerCase().includes(search);
                                    }).map(
                                users => 
                                <tr key = {users.username}>
                                    <td>{users.username}</td>
                                    <td style={{ color: "gray"}}>hidden</td>
                                    <td>{users.user_role}</td>
                                    <td>{assignedLabs[users.username]}</td>
                                    <td>
                                        <button onClick={() => deleteUser(users.username)} className="btn btn-danger">Delete</button>
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


export default UserListComponents;