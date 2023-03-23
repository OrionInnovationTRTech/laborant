import React, {useState, useEffect} from "react";
import {Link} from "react-router-dom";
import axios from "axios";
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import InputGroup from 'react-bootstrap/InputGroup';
import Table from 'react-bootstrap/Table';
import {checkAuthentication, getHeaders} from "../services/AuthHeader";
const base = process.env.REACT_APP_BASE_PATH || '';


const TeamListComponents = () => {
    checkAuthentication();
    const [teams, setTeams] = useState([]);
    const [search, setSearch] = useState('');
    const [assignedLabs, setAssignedLabs] = useState({});

    useEffect(() => {
        getAllTeams();

    }, []);

    useEffect(() => {
        getLabs();
    }, [teams]);

    const getAllTeams = () => {
        axios.get(`${process.env.REACT_APP_SPRING_HOST}/teams/`, getHeaders())
            .then((response) => {
                setTeams(response.data);
                console.log(response.data);
            }).catch((error) => {
            console.log(error);
        })
    }

    const deleteTeam = (name) => {
        if (window.confirm("Are you sure you want to delete this user?")) {
            axios.delete(`${process.env.REACT_APP_SPRING_HOST}/teams/delete/` + name, getHeaders())
                .then((response) => {
                    console.log(response);
                    window.location.reload();
                }).catch((error) => {
                console.log(error);
            })
        }
    }

    const getLabs = () => {
        teams.forEach(team => {axios.get(`${process.env.REACT_APP_SPRING_HOST}/v1/team-labs/${team.name}`, getHeaders())
            .then((response) => {
                setAssignedLabs((prevAssignedLabs) => ({...prevAssignedLabs, [team.name]: response.data.join('\n')}));
            }).catch((error) => {
                setAssignedLabs("Error");
            });
        })};


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
                        <td style={{fontSize: '20px'}}><b>Teams</b></td>
                        <td style={{fontSize: '20px'}}><b>Assigned Labs</b></td>
                        <td style={{fontSize: '20px'}}><b>Actions</b></td>
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
                                    <td>{assignedLabs[teams.name]}</td>
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