import React, {useState, useEffect} from "react";
import {Link} from "react-router-dom";
import axios from "axios";
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Table from 'react-bootstrap/Table';
import { getHeaders } from "../services/AuthHeader";


const TeamListComponents = () => {

    const [teams, setTeams] = useState([]);
    const [search, setSearch] = useState('');

    useEffect(() => {

        getAllTeams();
    }, []);

    const getAllTeams = () => {
        axios.get('http://localhost:8080/teams/', getHeaders())
            .then((response) => {
                setTeams(response.data);
                console.log(response.data);
            }).catch((error) => {
            console.log(error);
        })
    }

    const deleteTeam = (name) => {
        if (window.confirm("Are you sure you want to delete this user?")) {
            axios.delete("http://localhost:8080/teams/delete/" + name, getHeaders())
                .then((response) => {
                    console.log(response);
                    window.location.replace('/teams');
                }).catch((error) => {
                console.log(error);
            })
        }
    }


    return(
        <div>
            <h1 className = "text-center">Team List<Link to="/add-team" className="btn btn-primary" style={{marginLeft: '700px'}}>Add Team</Link></h1>

            <Container>
                <Form>
                    <InputGroup className='my-3'>
                        <Form.Control
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder='Filter teams...'
                        />
                    </InputGroup>
                </Form>
                <Table hover>
                    <thead>
                    <tr>
                        <td>Team Name</td>
                        <td>Assigned Labs</td>
                        <td>Actions</td>
                    </tr>
                    </thead>
                    <tbody>
                    {teams
                        .filter((teams) => {
                            return search.toLowerCase() === ''
                                ? teams
                                : teams.username.toLowerCase().includes(search);
                        }).map(
                            teams =>
                                <tr key = {teams.name}>
                                    <td>{teams.name}</td>
                                    <td>{teams.labs.map((lab) => lab.labName).join(', ')}</td>
                                    <td>
                                        <button onClick={() => deleteTeam(teams.name)} className="btn btn-danger">Delete</button>
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


export default TeamListComponents;