import React, {useState, useEffect} from "react";
import {checkAuthentication, getHeaders} from "../services/AuthHeader";
import axios from "axios";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import { InputGroup } from "react-bootstrap";

const AssignUsers = () => {
  checkAuthentication();

  const [labs, setLabs] = useState([]);
  const [selectedLab, setSelectedLab] = useState("");
  const [users, setUsers] = useState([]);
  const [selectedUsers, setSelectedUsers] = useState([]);
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
        setLabs(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleLabChange = (event) => {
    setSelectedLab(event.target.value);
    getUsers();
  };

  const getUsers = () => {
    axios
      .get("http://localhost:8080/users/", getHeaders())
      .then((response) => {
        const usersNotInLab = response.data.filter((user) => !user.labs.some((lab) => lab.labName === selectedLab));
        setUsers(usersNotInLab);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleUserChange = (event) => {
    setSelectedUsers(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = {
      username: selectedUsers,
      labName: selectedLab
    };
    axios.post(`http://localhost:8080/v1/assign`, {}, {
        headers: {
            'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
        },
        params: data
        })
      .then((response) => {
        setMessage("Successfully added users to lab.");
    })
      .catch((error) => {
        setMessage(`Failed to add user. Error: ${error.response.data.message}`);
    });
  };

  return (
    <div>
      <h1 className="text-center">Assign Users</h1>
      <Form onSubmit={handleSubmit}>
      <div className="d-flex flex-row w-100 mx-auto justify-content-center">
  <Form.Group >
    <Form.Label>Select Lab</Form.Label>
    <InputGroup>
      <Form.Control
        type="text"
        placeholder="Search for lab..."
        value={searchLab}
        onChange={(e) => setSearchLab(e.target.value)}
      />
    </InputGroup>
    <Form.Control as="select" multiple onChange={handleLabChange}>
      {labs
        .filter((lab) => lab.labName.includes(searchLab))
        .map((lab) => (
          <option key={lab.labName} value={lab.labName}>
            {lab.labName}
          </option>
        ))}
    </Form.Control>
  </Form.Group>
  <Form.Group>
    <Form.Label>Select User</Form.Label>
    <InputGroup>
      <Form.Control
        type="text"
        placeholder="Search for user..."
        value={searchUser}
        onChange={(e) => setSearchUser(e.target.value)}
      />
    </InputGroup>
    <Form.Control as="select" multiple onChange={handleUserChange}>
      {users
        .filter((user) => user.username.includes(searchUser))
        .map((user) => (
          <option key={user.username} value={user.username}>
            {user.username}
          </option>
        ))}
    </Form.Control>
  </Form.Group>
</div>
        <Button variant="primary" type="submit">
            Submit
        </Button>
        </Form>
        <p>{message}</p>
    </div>
    );
};

export default AssignUsers;

