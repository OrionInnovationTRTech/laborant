import axios from "axios";
import { getHeaders } from "./AuthHeader";

const LAB_REST_API_URL = 'http://localhost:8080/v1/labs'
const LAB_REST_API_ADD_URL = 'http://localhost:8080/v1/labs/add/'
const LAB_REST_API_UPDATE_URL = 'http://localhost:8080/v1/labs'

class LabService {
    getLabs(){
        return axios.get(LAB_REST_API_URL)
    }

    addLab(lab){
        return axios.post(LAB_REST_API_ADD_URL, lab,getHeaders())
    }
    
    updateLab(lab) {
        return axios.put(`${LAB_REST_API_UPDATE_URL}/${lab.labName}`, lab, getHeaders());
    }
    }


export default new LabService();