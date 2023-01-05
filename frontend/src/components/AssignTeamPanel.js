import React, { useState, useEffect } from "react";
import { checkAuthentication, getHeaders } from "../services/AuthHeader";
import axios from "axios";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import { InputGroup } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const AssignTeamPanel = () => {
    checkAuthentication();

    const navigate = useNavigate();
    const [labs, setLabs] = useState([]);
    const [selectedLab, setSelectedLab] = useState("");
    const [teams, setTeams] = useState([]);
    const [selectedTeam, setSelectedTeam] = useState([]);
    const [searchLab, setSearchLab] = useState("");
    const [searchTeam, setSearchTeam] = useState("");
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
                    isAssigned: lab.teams.length > 0,
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
        getTeams();
    }, [selectedLab]);

    const getTeams = () => {
        axios
            .get("http://localhost:8080/teams/", getHeaders())
            .then((response) => {
                const selectedLabObject = labs.find(lab => lab.labName === selectedLab);
                const selectedLabId = selectedLabObject.id;
                const teamsInLab = response.data.map((team) => {
                    const isAssigned = team.labs.some((lab) => lab.id === selectedLabId) || team.labs.includes(selectedLabId);
                    return {
                        ...team,
                        isAssigned,
                    };
                });
                setTeams(teamsInLab);
            })
            .catch((error) => {
                console.log(error);
            });
    };

    const handleTeamChange = (event) => {
        setSelectedTeam(event.target.value);
    };

    const handleAssign = (event) => {
        event.preventDefault();
        const data = {
            teamName: selectedTeam,
            labName: selectedLab
        };
        axios.put(`http://localhost:8080/v1/assign-team`, {}, {
            headers: {
                'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
            },
            params: data
        })
            .then((response) => {
                setMessage("Successfully assigned team: " + selectedTeam + " to lab: " + selectedLab);
                getAllLabs();
                getTeams();
            })
            .catch((error) => {
                setMessage(`Failed to add team. Error: ${error.response.data.message}`);
            });
    };

    const handleUnassign = (event) => {
        event.preventDefault();
        const data = {
            teamName: selectedTeam,
            labName: selectedLab
        };
        axios.put(`http://localhost:8080/v1/unassign-team`, {}, {
            headers: {
                'Authorization': 'Basic ' + btoa(localStorage.getItem('username') + ':' + localStorage.getItem('password')),
            },
            params: data
        })
            .then((response) => {
                setMessage("Successfully unassigned team: " + selectedTeam + " from lab: " + selectedLab);
                getAllLabs();
                getTeams();
            })
            .catch((error) => {
                setMessage(`Failed to unassign team. Error: ${error.response.data.message}`);
            });
    };

    return (
        <div>
            <h2 className="text-center">ASSIGN TEAMS PANEL</h2>
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
                                placeholder="Search for team..."
                                value={searchTeam}
                                onChange={(e) => setSearchTeam(e.target.value)}
                            />
                        </InputGroup>
                        <Form.Control as="select" style={{ height: "200px" }} multiple onChange={handleTeamChange}>
                            {teams
                                .filter((team) => team.name.includes(searchTeam))
                                .map((team) => (
                                    <option
                                        key={team.name}
                                        value={team.name}
                                        style={{ color: team.isAssigned ? "gray" : "black" }}
                                    >
                                        {team.name}
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


export default AssignTeamPanel;


