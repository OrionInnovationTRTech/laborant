import React, {useState, useEffect, version} from "react";
import Table from 'react-bootstrap/Table';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import {Link} from "react-router-dom";
import { checkAuthentication, getHeaders } from "../services/AuthHeader";
import axios from "axios";



const AdminPanelComponent = () => {
    checkAuthentication();


    const [labs, setLabs] = useState([]);
    const [search, setSearch] = useState('');
    const [assignedUsers, setAssignedUsers] = useState({});



    useEffect(() => {

        getAllLabs();
    }, []);

    useEffect(() => {
        getUsers();
    },[labs]);

    const getUsers = () => {
        labs.forEach(lab => {axios.get(`http://localhost:8080/v1/lab-users/${lab.labName}`, getHeaders())
        .then((response) => {
        setAssignedUsers((prevAssignedUsers) => ({...prevAssignedUsers, [lab.labName]: response.data.join('\n')}));
        }).catch((error) => {
            setAssignedUsers("Error");
        });
    })};    

    const getAllLabs = () => {
        axios.get('http://localhost:8080/v1/labs/', getHeaders())
        .then((response) => {
            setLabs(response.data);
            console.log(response.data);
        }).catch((error) => {
            console.log(error);
        })
    };

    const deleteLab = (labName) => {
if (window.confirm("Are you sure you want to delete this lab?")) {
    axios.delete("http://localhost:8080/v1/labs/" + labName, getHeaders())
        .then((response) => {
            console.log(response);
            window.location.replace('/labs');
        }).catch((error) => {
            console.log(error);
        })
}
}

        return(
            <div>
                <h1 className = "text-center"> Admin Panel
                <Link to ="/" className="btn btn-secondary" style={{marginLeft: '100px'}}>Lab Panel</Link>
                <Link to="/panel/assign-users" className="btn btn-success">Assign Users</Link>
                <Link to="/panel/assign-teams" className="btn btn-danger">Assign Teams</Link>
                <Link to="/users" className="btn btn-info">Users Panel</Link>
                </h1>
            

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
                            <td>Assigned Users</td>
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
                                    assignedUsers[labs.labName].toLowerCase().includes(search);

                                }).map(
                                labs =>
                                <tr key={labs.labName}>
                                    <td>{labs.labName}</td>
                                    <td>{labs.userName}</td>
                                    <td>{labs.host}</td>
                                    <td>{assignedUsers[labs.labName]}</td>
                                    <td>
                                        <button onClick={() => deleteLab(labs.labName)} className="btn btn-danger">Delete</button>
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