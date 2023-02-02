import React ,{useState} from "react";
import { getHeaders } from "../services/AuthHeader";
import axios from "axios";
import {checkAuthentication} from "../services/AuthHeader";


const AddTeamComponent = () => {
    checkAuthentication();
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [name, setName] = useState('');

    const saveUser = (e) => {
        e.preventDefault();
        let team = {name: name};

        console.log("requesting  " + JSON.stringify(team));
        axios.post(`${process.env.REACT_APP_SPRING_HOST}/teams/add`,team,getHeaders())
            .then((response) => {
                console.log(response.status);
                if (response.status === 200) {
                    setMessage('Team Added Successfully. Redirecting...');
                    setTimeout(() => {
                        window.location.replace('/teams');
                    }, 1500);
                } else if (response.status === 400) {
                    setError(`Failed to add team. ${response.data.message}`);
                } else {
                    setError(`Failed to add team. HTTP status code: ${response.status}`);
                }
            }).catch((error) => {
            setError(`Failed to add team. Error: ${error.response.data.message}`);
        });

        console.log('team => ' + JSON.stringify(team));
    }

    return(
        <div className="container">
            <div className="row">
                <div className="card col-md-6 offset-md-3 offset-md-3">
                    <h3 className="text-center">Add Team</h3>
                    <div className="card-body">
                        <form>
                            <div className="form-group">
                                <label className = "form-label">Name:</label>
                                <input
                                    type = "text"
                                    placeholder = "Enter Team Name"
                                    name = "name"
                                    className = "form-control"
                                    value = {name}
                                    onChange = {e => setName(e.target.value)}
                                >
                                </input>
                            </div>
                            <div>
                                {error && <div style={{color: 'red'}}>{error}</div>}
                                {message && <div style={{color: 'green'}}>{message}</div>}
                            </div>
                            <button className = "btn btn-success" onClick={(e) => saveUser(e)}>Save</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
export default AddTeamComponent;