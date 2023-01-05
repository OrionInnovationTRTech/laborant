import React, {useState, useEffect} from "react";
import {checkAuthentication, getHeaders} from "../services/AuthHeader";
import axios from "axios";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import { InputGroup } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const AssignUsers = () => {
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
        setLabs(response.data);
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
        const usersNotInLab = response.data.filter((user) => {
          return !user.labs.some((lab) => lab.id === selectedLabId) && !user.labs.includes(selectedLabId);
        });
        setUsers(usersNotInLab);
        
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleUserChange = (event) => {
    setSelectedUser(event.target.value);
  };

  const handleGoBack = () => {
    navigate(-1);
  }

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
        setSelectedUser("");
    })
      .catch((error) => {
        setMessage(`Failed to add user. Error: ${error.response.data.message}`);
        setSelectedUser("");
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
                'Authorization' : 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
            },
            params: data
        })
        .then((response) => {
            setMessage("Successfully unassigned user: " + selectedUser + " from lab: " + selectedLab);
            setSelectedUser("");
        }
        )
        .catch((error) => {
            setMessage(`Failed to remove user. Error: ${error.response.data.message}`);
            setSelectedUser("");
        });
        }


  return (
    <div>
      <h1 className="text-center">Assign Users</h1>
      <Form onSubmit={handleAssign}>
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
        <Button variant="primary" onClick={handleAssign}>
            Assign
        </Button>
        <Button variant="primary" onClick={handleUnassign}>
            Unassign
        </Button>
        <Button variant="secondary" onClick={handleGoBack}>
          Go Back
        </Button>
        </Form>
        <p>{message}</p>
    </div>
    );
};

export default AssignUsers;

