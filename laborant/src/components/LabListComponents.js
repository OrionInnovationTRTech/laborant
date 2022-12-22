import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {checkAuthentication, getHeaders} from "../services/AuthHeader";
import axios from "axios";


const LabListComponents = () => {
        checkAuthentication();
        
        
        const [labs, setLabs] = useState([]);
        const [labVersion, setLabVersion] = useState({});

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
        useEffect(() => {
            let labVersion = "CAN'T CONNECT";
            labs.forEach(lab => {
              axios.get(`http://localhost:8080/v1/labs/runCommand/${lab.labName}?command=sudo+wae-status`, getHeaders())
                .then((response) => {
                    const data = response.data.split('\n').map(row => {
                        return row.split(' ').filter(element => element !== '');

                      }).filter(row => row.length > 0);
                        console.log(data);
                        if (data[0][1] === "connect") {
                            console.log(labVersion);
                        }
                        else{
                            labVersion = data[1][1];
                            console.log(labVersion);
                        }
                    setLabVersion((prevLabVersions) => ({ ...prevLabVersions, [lab.labName]: labVersion }));
                }).catch((error) => {
                    labVersion = ("CAN'T CONNECT");
                    setLabVersion((prevLabVersions) => ({ ...prevLabVersions, [lab.labName]: labVersion }));
              });

            });
          }, [labs]);
        

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

            return(
                <div>
                    <h1 className = "text-center"> Lab List<Link to="/add-lab" className="btn btn-primary" style={{marginLeft: '700px'}}>Add Lab</Link></h1>

                    <table className="table table-striped">
                        <thead>
                            <tr>
                                <td>Name</td>
                                <td>Username</td>
                                <td>Password</td>
                                <td>Host</td>
                                <td>Port</td>
                                <td>Version</td>
                                
                            </tr>
                        </thead>
                        <tbody>
                        {
                                labs.map(
                                    labs => 
                                    <tr key={labs.labName}>
                                        <td>{labs.labName}</td>
                                        <td>{labs.userName}</td>
                                        <td>{labs.password}</td>
                                        <td>{labs.host}</td>
                                        <td>{labs.port}</td>
                                        <td>{labVersion[labs.labName]}</td>
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