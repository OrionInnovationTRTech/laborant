import React ,{useState} from "react";
import LabService from "../services/LabService";
import {Navigate, useNavigate} from "react-router-dom";

const AddLabComponent = () => {

    const [message, setMessage] = useState('');
    const [labName, setLabName] = useState('');
    const [userName, setUserName] = useState('');
    const [password, setPassword] = useState('');
    const [host, setHost] = useState('');
    const [port, setPort] = useState('');
    const history = useNavigate();

    const saveLab = (e) => {
        e.preventDefault();
        let lab = {labName: labName, userName: userName, password: password, host: host, port: port};
        
        LabService.addLab(lab).then((response) => {
            console.log(response.status);
            if (response.status === 200) {
                setMessage(<p style={{color: 'green'}}>Lab Added Successfully. Redirecting...'</p>);
                setTimeout(() => {
                    window.location.replace('/labs');
                  }, 1500);
              } else if (response.status === 400) {
                setMessage(`Failed to add lab. ${response.data.message}`);
              } else {
                setMessage(`Failed to add lab. HTTP status code: ${response.status}`);
              }
            }).catch((error) => {
                setMessage(`Failed to add lab. Error: ${error.response.data.message}`);
            });
          
        console.log('lab => ' + JSON.stringify(lab));
    }

    return(
        <div className="container">
            <div className="row">
                <div className="card col-md-6 offset-md-3 offset-md-3">
                    <h3 className="text-center">Add Lab</h3>
                    <div className="card-body">
                        <form>
                            <div className="form-group">
                                <label className = "form-label">Lab Name:</label>
                                <input
                                    type = "text"
                                    placeholder = "Enter Lab Display Name"
                                    name = "labName" 
                                    className = "form-control"
                                    value = {labName} 
                                    onChange = {e => setLabName(e.target.value)}
                                >
                                </input>
                                </div>

                                <div className="form-group">
                                <label className = "form-label">Lab Username:</label>
                                <input
                                    type = "text"
                                    placeholder = "Enter Lab Username"
                                    name = "userName"
                                    className = "form-control"
                                    value = {userName}
                                    onChange = {e => setUserName(e.target.value)}
                                >
                                </input>
                                </div>
                                <div className="form-group">
                                <label className = "form-label">Lab Password:</label>
                                <input
                                    type = "text"
                                    placeholder = "Enter Lab Password"
                                    name = "password"
                                    className = "form-control"
                                    value = {password}
                                    onChange = {e => setPassword(e.target.value)}
                                >
                                </input>
                                </div>
                                <div className="form-group">
                                <label className = "form-label">Lab Host:</label>
                                <input
                                    type = "text"
                                    placeholder = "Enter Lab Host"
                                    name = "host"
                                    className = "form-control"
                                    value = {host}
                                    onChange = {e => setHost(e.target.value)}
                                >
                                </input>
                                </div>
                                <div className="form-group">
                                <label className = "form-label">Lab Port:</label>
                                <input
                                    type = "integer"
                                    placeholder = "Enter Lab Port"
                                    name = "port"
                                    className = "form-control"
                                    value = {port}
                                    onChange = {e => setPort(e.target.value)}
                                >
                                </input>
                                </div>
                                <div>
                                 {message}
                                </div>
                                <button className = "btn btn-success" onClick={(e) => saveLab(e)}>Save</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
export default AddLabComponent;