import './App.css';
import ReservationListComponent from './components/ReservationListComponent';
import Header from './components/Header';
import Footer from './services/Footer';
import {BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AddLabComponent from './components/AddLabComponent';
import LoginForm from './components/Login';
import {checkAuthentication} from './services/AuthHeader'
import EditLabComponent from './components/EditLabComponent';
import RunStatusComponent from './helpers/RunStatusModal';
import BulkAddLabComponent from './components/BulkAddLabComponent';
import UserListComponents from './components/UserListComponents';
import AddUserComponent from './components/AddUserComponent';
import NotFound from './components/NotFound';
import NotAuthorized from './components/NotAuthorized'
import AdminPanelComponent from './components/AdminPanelComponent';
import AssignUsersPanel from './components/AssignUserPanel';
import AssignTeamPanel from "./components/AssignTeamPanel";
import AddTeamComponent from "./components/AddTeamComponent";
import TeamListComponents from "./components/TeamListComponents";
import ChangePasswordComponent from "./components/ChangePasswordComponent";
import LabReservationForm from "./helpers/LabReservationComponent";
import LabListComponent from "./components/LabListComponent";
function App() {
  checkAuthentication();
  function isAdmin() {
    if (localStorage.getItem('isAdmin') === 'true') {
      return true;
    } else {
      return false;
    }
  }  
  return (
    <div className="App">
      <Router>
      <Header />
      <div className= "container">
        <Routes>
          <Route exact path = "/" element = {<ReservationListComponent/>}></Route>
          <Route path = "/labs" element = {<ReservationListComponent/>}></Route>
          <Route path = "/add-lab" element = { isAdmin() ? <AddLabComponent/> : <NotAuthorized/>}></Route>
          <Route path="/login" element={<LoginForm/>}></Route>
          <Route path="/edit-lab/:labName" element={<EditLabComponent/>}></Route> 
          <Route path="/run-command/:labName" element={<RunStatusComponent/>}></Route>
          <Route path="/reserve-lab/:labName" element={<LabReservationForm/>}></Route>
          <Route path="/bulk-add" element={
              isAdmin() ? <BulkAddLabComponent/> : <NotAuthorized/>
          }></Route>
          <Route path="/lab-list" element={<LabListComponent/>}></Route>}
          <Route path="panel/" element={
              isAdmin() ? (
                <AdminPanelComponent/>
              ) : (
                <NotAuthorized/>
              )
          }
          />
          <Route path="panel/assign-users/" element={
              isAdmin() ? (
                <AssignUsersPanel/>
              ) : (
                <NotAuthorized/>
              )
          }
          />
          <Route path="panel/assign-teams/" element={
              isAdmin() ? ( 
                <AssignTeamPanel/>
              ) : (
                <NotAuthorized/>
              )
          }
          />
          <Route path='/users' element={
              isAdmin() ? (
                <UserListComponents />
              ) : (
                <NotAuthorized/>
              )
            }
          />
          <Route path='/add-team' element={
                isAdmin() ? (
                    <AddTeamComponent />
                ) : (
                    <NotAuthorized/>
                )
            }
            />
          <Route path='/add-user'  element={
              isAdmin() ? (
                <AddUserComponent />
              ) : (
                <NotAuthorized/>
              )
            }
            />
            <Route path='/teams' element={
                isAdmin() ? (
                    <TeamListComponents />
                ) : (
                    <NotAuthorized/>
                )
            }
            />
            <Route path='/change-password' element={<ChangePasswordComponent/>}></Route>
          <Route path="*" element={<NotFound/>}></Route>          
        </Routes>
      </div>   
      <Footer />
      </Router>
    </div>
  );
}

export default App;
