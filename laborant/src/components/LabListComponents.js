    import React, {useState, useEffect} from "react";
    import LabService from "../services/LabService";
    import {Link} from "react-router-dom";
    import { checkAuthentication, getHeaders } from "../services/AuthHeader";
    import axios from "axios";
    import EditLabComponent from "./EditLabComponent";

    const LabListComponents = () => {
        checkAuthentication();
        
        
        const [labs, setLabs] = useState([]);

        useEffect(() => {


            getAllLabs();
        }, []);

        const getAllLabs = () => {
            axios.get('http://localhost:8080/v1/labs/', getHeaders())
            .then((response) => {
                setLabs(response.data);
                console.log(response.data);
            }).catch((error) => {
                console.log(error);
            })
        }

        const deleteLab = (labName) => {
    if (window.confirm("Are you sure you want to delete this lab?")) {
        axios.delete("http://localhost:8080/v1/labs/" + labName, getHeaders())
            .then((response) => {
                console.log(response);
                window.location.replace('/labs');
            }).catch((error) => {
                console.log(error);
            })
    }
}
            const editLab = (labName) => {
                // Navigate to the edit lab page and pass the labName as a prop
                window.location.replace(`/edit-lab/${labName}`);
            }
  

            return(
                <div>
                    <h1 className = "text-center"> Lab List<Link to="/add-lab" className="btn btn-primary" style={{marginLeft: '700px'}}>Add Lab</Link></h1>

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
                                    <tr key={labs.id}>
                                        <td>{labs.id}</td>
                                        <td>{labs.labName}</td>
                                        <td>{labs.userName}</td>
                                        <td>{labs.password}</td>
                                        <td>{labs.host}</td>
                                        <td>{labs.port}</td>
                                        <td>
                                            <button onClick={() => deleteLab(labs.labName)} className="btn btn-danger">Delete</button>
                                            <Link className="btn btn-secondary" to={`/edit-lab/${labs.labName}`}>Edit</Link>
                                            <Link className="btn btn-success" to={`/run-command/${labs.labName}`}>Run Command</Link>
                                        </td>
                                    </tr>
                                )
                            }

                        </tbody>
                    </table>
        
                    

                </div>

            )
        }


    export default LabListComponents;