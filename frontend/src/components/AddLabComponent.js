import React, {useState} from "react";
import LabService from "../services/LabService";
import {Link, useNavigate} from "react-router-dom";

const AddLabComponent = () => {

    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [labName, setLabName] = useState('');
    const [teamName, setTeamName] = useState('');
    const [userName, setUserName] = useState('');
    const [userEmail, setUserEmail] = useState('');
    const [password, setPassword] = useState('');
    const [host, setHost] = useState('');
    const [port, setPort] = useState('');
    const navigate = useNavigate();

    const saveLab = (e) => {
        e.preventDefault();
        let lab = {
            labName: labName,
            userName: userName,
            password: password,
            host: host,
            port: port,
            teamName: teamName,
            userEmail: userEmail
        };

        LabService.addLab(lab).then((response) => {
            console.log(response.status);
            if (response.status === 200) {
                setMessage('Lab Added Successfully. Redirecting...');
                setTimeout(() => {
                    navigate('/labs');
                }, 1500);
            } else if (response.status === 400) {
                setError(`Failed to add lab. ${response.data.message}`);
            } else {
                setError(`Failed to add lab. HTTP status code: ${response.status}`);
            }
        }).catch((error) => {
            setError(`Failed to add lab. Error: ${error.response.data.message}`);
        });

        console.log('lab => ' + JSON.stringify(lab));
    }

    return (
        <div className="container">
            <div className="row">
                <div className="card col-md-6 offset-md-3 offset-md-3">
                    <br/>
                    <h3 className="text-center">Add Lab<Link to="/bulk-add-lab"
                                                             className={"btn btn-primary float-right"}
                                                             style={{marginLeft: '100px'}}>Bulk Add</Link></h3>
                    <div className="card-body">
                        <form>
                            <div className="form-group">
                                <label className="form-label">Lab Name:</label>
                                <input
                                    type="text"
                                    placeholder="Enter Lab Display Name"
                                    name="labName"
                                    className="form-control"
                                    value={labName}
                                    onChange={e => setLabName(e.target.value)}
                                >
                                </input>
                            </div>

                            <div className="form-group">
                                <label className="form-label">Lab Username:</label>
                                <input
                                    type="text"
                                    placeholder="Enter Lab Username"
                                    name="userName"
                                    className="form-control"
                                    value={userName}
                                    onChange={e => setUserName(e.target.value)}
                                >
                                </input>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Lab Password:</label>
                                <input
                                    type="text"
                                    placeholder="Enter Lab Password"
                                    name="password"
                                    className="form-control"
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                >
                                </input>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Lab Host:</label>
                                <input
                                    type="text"
                                    placeholder="Enter Lab Host"
                                    name="host"
                                    className="form-control"
                                    value={host}
                                    onChange={e => setHost(e.target.value)}
                                >
                                </input>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Lab Port:</label>
                                <input
                                    type="integer"
                                    placeholder="Enter Lab Port"
                                    name="port"
                                    className="form-control"
                                    value={port}
                                    onChange={e => setPort(e.target.value)}
                                >
                                </input>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Assign Team (optional):</label>
                                <input
                                    type="text"
                                    placeholder="Enter Team Name"
                                    name="teamName"
                                    className="form-control"
                                    value={teamName}
                                    onChange={e => setTeamName(e.target.value)}
                                >
                                </input>
                            </div>
                            <div className="form-group">
                                <label className="form-label">Assign User (optional):</label>
                                <input
                                    type="text"
                                    placeholder="Enter User Email "
                                    name="userEmail"
                                    className="form-control"
                                    value={userEmail}
                                    onChange={e => setUserEmail(e.target.value)}
                                >
                                </input>
                            </div>
                            <div>
                                {error && <div style={{color: 'red'}}>{error}</div>}
                                {message && <div style={{color: 'green'}}>{message}</div>}
                            </div>
                            <button className="btn btn-success" onClick={(e) => saveLab(e)}>Save</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
export default AddLabComponent;