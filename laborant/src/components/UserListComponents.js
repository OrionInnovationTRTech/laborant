import React, {useState, useEffect} from "react";
import LabService from "../services/LabService";
import {Link} from "react-router-dom";

const UserListComponents = () => {
    
    const [users, setUsers] = useState([]);

    useEffect(() => {

        getAllUsers();
    }, []);

    const getAllUsers = () => {
         UserService.getUsers({
            headers: {
              // Attach the cookie to the request headers
              Cookie: cookie,
            },
          }).then((response) => {
            setLabs(response.data);
            console.log(response.data);
        }).catch((error) => {
            console.log(error);
        })
    }


        return(
            <div>
                <h1 className = "text-center">User List</h1>
                <Link to = "/add-user" className = "btn btn-primary">Add User</Link>
                <table className="table table-striped">
                    <thead>
                        <tr>
                            <td>User Id</td>
                            <td>Username</td>
                            <td>Password</td>
                            <td>Role</td>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            users.map(
                                users => 
                                <tr key = {users.id}>
                                    <td>{users.id}</td>
                                    <td>{users.username}</td>
                                    <td>{users.password}</td>
                                    <td>{users.user_role}</td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
    
                

            </div>

        )
    }


export default LabListComponents;