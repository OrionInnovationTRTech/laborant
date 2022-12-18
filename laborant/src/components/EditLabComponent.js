import React ,{useState} from "react";
import LabService from "../services/LabService";

const EditLabComponent = () => {

    const [message, setMessage] = useState('');
    const [labName] = useState(window.location.pathname.split('/')[2]);
    const [userName, setUserName] = useState('');
    const [password, setPassword] = useState('');
    const [host, setHost] = useState('');
    const [port, setPort] = useState('');

    const saveLab = (e) => {
        e.preventDefault();
        let lab = {labName: labName, userName: userName, password: password, host: host, port: port};
        
        LabService.updateLab(lab).then((response) => {
            console.log(response.status);
            if (response.status === 200) {
               setMessage(<p style={{color: 'green'}}>Lab Updated Successfully. Redirecting...</p>)
                      setTimeout(() => {
                  window.location.replace('/labs');
                }, 1500);
              } else if (response.status === 400) {
                setMessage(`Failed to update lab. ${response.data.message}`);
              } else {
                setMessage(`Failed to update lab. HTTP status code: ${response.status}`);
              }
            }).catch((error) => {
                setMessage(`Failed to update lab. Error: ${error.response.data.message}`);
            });
        console.log('lab => ' + JSON.stringify(lab));
    }

    return(
        <div className="container">
            <div className="row">
                <div className="card col-md-6 offset-md-3 offset-md-3">
                <h3 className="text-center">Edit Lab Credentials</h3>
                    <h4 className="text-center">Lab: {labName}</h4>
                    <div className="card-body">
                        <form>
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
export default EditLabComponent;