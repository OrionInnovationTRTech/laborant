import React, {useState, useEffect} from "react";
import LabService from "../services/LabService";
import {Link} from "react-router-dom";
import { checkAuthentication, getHeaders } from "../services/AuthHeader";
import axios from "axios";
import Cookies from "js-cookie";
const LabListComponents = () => {
    checkAuthentication();
    
    
    const [labs, setLabs] = useState([]);

    useEffect(() => {


        getAllLabs();
    }, []);

    const getAllLabs = () => {

        console.log('storage id: '+ localStorage.getItem('sessionId'));
        console.log('cookie id: ' + Cookies.get('JSESSIONID'))

        axios.get('http://localhost:8080/v1/labs/', { 
            withCredentials:false
         })
          .then((response) => {
            setLabs(response.data);
            console.log(response.data);
        }).catch((error) => {
            console.log(error);
        })
    }


        return(
            <div>
                <h1 className = "text-center"> Lab List</h1>
                <Link to = "/add-lab" className = "btn btn-primary">Add Lab</Link>
                <table className="table table-striped">
                    <thead>
                        <tr>
                            <td>Lab Id</td>
                            <td>Lab Name</td>
                            <td>Lab Username</td>
                            <td>Lab Password</td>
                            <td>Lab Host</td>
                            <td>Lab Port</td>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            labs.map(
                                labs => 
                                <tr key = {labs.id}>
                                    <td>{labs.id}</td>
                                    <td>{labs.labName}</td>
                                    <td>{labs.userName}</td>
                                    <td>{labs.password}</td>
                                    <td>{labs.host}</td>
                                    <td>{labs.port}</td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
    
                

            </div>

        )
    }


export default LabListComponents;