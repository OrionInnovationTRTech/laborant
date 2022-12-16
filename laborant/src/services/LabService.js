import axios from "axios";

const LAB_REST_API_URL = 'http://localhost:8080/v1/labs'
const LAB_REST_API_ADD_URL = 'http://localhost:8080/v1/labs/add/'

class LabService {
    getLabs(){
        return axios.get(LAB_REST_API_URL)
    }

    addLab(lab){
        return axios.post(LAB_REST_API_ADD_URL, lab)
    }
}

export default new LabService();