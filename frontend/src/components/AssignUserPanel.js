import React, { useState, useEffect } from "react";
import { checkAuthentication, getHeaders } from "../services/AuthHeader";
import axios from "axios";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import { InputGroup } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const AssignUsersPanel = () => {
    checkAuthentication();

    const navigate = useNavigate();
    const [labs, setLabs] = useState([]);
    const [selectedLab, setSelectedLab] = useState("");
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState([]);
    const [searchLab, setSearchLab] = useState("");
    const [searchUser, setSearchUser] = useState("");
    const [message, setMessage] = useState("");

    useEffect(() => {
        getAllLabs();
    }, []);

    const getAllLabs = () => {
        axios
            .get("http://localhost:8080/v1/labs/", getHeaders())
            .then((response) => {
                const updatedLabs = response.data.map((lab) => ({
                    ...lab,
                    isAssigned: lab.users.length > 0,
                }));
                setLabs(updatedLabs);
            })
            .catch((error) => {
                console.log(error);
            });
    };

    const handleLabChange = (event) => {
        setSelectedLab(event.target.value);
    };

    useEffect(() => {
        getUsers();
    }, [selectedLab]);

    const getUsers = () => {
        axios
            .get("http://localhost:8080/users/", getHeaders())
            .then((response) => {
                const selectedLabObject = labs.find(lab => lab.labName === selectedLab);
                const selectedLabId = selectedLabObject.id;
                const usersInLab = response.data.map((user) => {
                    const isAssigned = user.labs.some((lab) => lab.id === selectedLabId) || user.labs.includes(selectedLabId);
                    return {
                        ...user,
                        isAssigned,
                    };
                });
                setUsers(usersInLab);
            })
            .catch((error) => {
                console.log(error);
            });
    };

    const handleUserChange = (event) => {
        setSelectedUser(event.target.value);
    };

    const handleAssign = (event) => {
        event.preventDefault();
        const data = {
            username: selectedUser,
            labName: selectedLab
        };
        axios.put(`http://localhost:8080/v1/assign-user`, {}, {
            headers: {
                'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
            },
            params: data
        })
            .then((response) => {
                setMessage("Successfully assigned user: " + selectedUser + " to lab: " + selectedLab);
                getAllLabs();
                getUsers();
            })
            .catch((error) => {
                setMessage(`Failed to add user. Error: ${error.response.data.message}`);
            });
    };

    const handleUnassign = (event) => {
        event.preventDefault();
        const data = {
            username: selectedUser,
            labName: selectedLab
        };
        axios.put(`http://localhost:8080/v1/unassign-user`, {}, {
            headers: {
                'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
            },
            params: data
        })
            .then((response) => {
                setMessage("Successfully unassigned user: " + selectedUser + " from lab: " + selectedLab);
                getAllLabs();
                getUsers();
            })
            .catch((error) => {
                setMessage(`Failed to unassign user. Error: ${error.response.data.message}`);
            });
    };

    return (
        <div>
            <h2 className="text-center">ASSIGN USERS PANEL</h2>
            <Form>
                <div className="d-flex w-100 mx-auto justify-content-center">
                    <div className="d-flex flex-column">
                        <InputGroup>
                            <Form.Control
                                type="text"
                                placeholder="Search for lab..."
                                value={searchLab}
                                onChange={(e) => setSearchLab(e.target.value)}
                            />
                        </InputGroup>
                        <Form.Control as="select" style={{ height: "200px" }} multiple onChange={handleLabChange}>
                            {labs
                                .filter((lab) => lab.labName.includes(searchLab))
                                .map((lab) => (
                                    <option
                                        key={lab.labName}
                                        value={lab.labName}
                                        style={{ color: lab.isAssigned ? "gray" : "black" }}
                                    >
                                        {lab.labName}
                                    </option>
                                ))}
                        </Form.Control>
                    </div>
                    <div className="d-flex flex-column">
                        <InputGroup>
                            <Form.Control
                                type="text"
                                placeholder="Search for user..."
                                value={searchUser}
                                onChange={(e) => setSearchUser(e.target.value)}
                            />
                        </InputGroup>
                        <Form.Control as="select" style={{ height: "200px" }} multiple onChange={handleUserChange}>
                            {users
                                .filter((user) => user.username.includes(searchUser))
                                .map((user) => (
                                    <option
                                        key={user.username}
                                        value={user.username}
                                        style={{ color: user.isAssigned ? "gray" : "black" }}
                                    >
                                        {user.username}
                                    </option>
                                ))}
                        </Form.Control>
                    </div>
                </div>
                <div className="text-center mt-4">
                    <Button variant="primary" type="submit" onClick={handleAssign}>
                        Assign
                    </Button>
                    <Button variant="secondary" type="submit" onClick={handleUnassign}>
                        Unassign
                    </Button>
                    <Button variant={"danger"} onClick={() => navigate(-1)}>
                        Go Back
                    </Button>
                </div>
            </Form>
            {message && <p className="text-center mt-4">{message}</p>}
        </div>
    );
};


    export default AssignUsersPanel;


