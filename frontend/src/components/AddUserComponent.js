import React ,{useState} from "react";
import {checkAuthentication, getHeaders} from "../services/AuthHeader";
import axios from "axios";


const AddUserComponent = () => {
    checkAuthentication();
    const [message, setMessage] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const saveUser = (e) => {
        e.preventDefault();
        let user = {username: username,password:password,user_role:'USER'};
        
        axios.post('http://localhost:8080/users/add',user,getHeaders())
        .then((response) => {
            console.log(response.status);
            if (response.status === 200) {
                setMessage(<p style={{color: 'green'}}>User Added Successfully. Redirecting...</p>);
                setTimeout(() => {
                    window.location.replace('/users');
                  }, 1500);
              } else if (response.status === 400) {
                setMessage(`Failed to add user. ${response.data.message}`);
              } else {
                setMessage(`Failed to add user. HTTP status code: ${response.status}`);
              }
            }).catch((error) => {
                setMessage(`Failed to add user. Error: ${error.response.data.message}`);
            });
          
        console.log('user => ' + JSON.stringify(user));
    }

    return(
        <div className="container">
            <div className="row">
                <div className="card col-md-6 offset-md-3 offset-md-3">
                    <h3 className="text-center">Add User</h3>
                    <div className="card-body">
                        <form>
                            <div className="form-group">
                                <label className = "form-label">Username:</label>
                                <input
                                    type = "text"
                                    placeholder = "Enter Username"
                                    name = "username" 
                                    className = "form-control"
                                    value = {username} 
                                    onChange = {e => setUsername(e.target.value)}
                                >
                                </input>
                                </div>

                                <div className="form-group">
                                <label className = "form-label">Password:</label>
                                <input
                                    type = "text"
                                    placeholder = "Enter Password"
                                    name = "password"
                                    className = "form-control"
                                    value = {password}
                                    onChange = {e => setPassword(e.target.value)}
                                >
                                </input>
                                </div>
                                <div>
                                 {message}
                                </div>
                                <button className = "btn btn-success" onClick={(e) => saveUser(e)}>Save</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
export default AddUserComponent;