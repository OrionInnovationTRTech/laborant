import './App.css';
import ReservationListComponent from './components/ReservationListComponent';
import Header from './components/Header';
import Footer from './services/Footer';
import {BrowserRouter as Router, Routes, Route, useNavigate} from 'react-router-dom';
import AddLabComponent from './components/AddLabComponent';
import LoginForm from './components/Login';
import EditLabComponent from './components/EditLabComponent';
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
import AccountDashboard from "./components/AccountDashboard";
import LabListComponent from "./components/LabListComponent";
import ForgotPasswordComponent from "./components/ForgotPasswordComponent";
import BulkAddUserComponent from "./components/BulkAddUserComponent";
import {useEffect} from "react";
function App() {
  function isAdmin() {
    if (localStorage.getItem('isAdmin') === 'true') {
      return true;
    } else {
      return false;
    }
  }

    function CheckAuthentication() {
        const navigate = useNavigate();

        useEffect(() => {
            if (!localStorage.getItem("isAuthenticated") && window.location.pathname !== "/login") {
                navigate("/login");
            }
            else {
                if (!localStorage.getItem("hasEmail")  && window.location.pathname !== "/dashboard") {
                    navigate("/dashboard");
                }
            }
        }, [navigate]);

        return null;
    }
  return (
    <div className="App">
      <Router basename={"laborant"}>
          <CheckAuthentication />
      <Header />
      <div className= "container">
        <Routes>
          <Route path = "/" element = {<ReservationListComponent/>}></Route>
          <Route path = "/labs" element = {<ReservationListComponent/>}></Route>
          <Route path = "/add-lab" element = { isAdmin() ? <AddLabComponent/> : <NotAuthorized/>}></Route>
          <Route path="/login" element={<LoginForm/>}></Route>
            <Route path="/forgot-password" element={<ForgotPasswordComponent/>}></Route>
            <Route path="/approve-email" element={<AccountDashboard/>}></Route>
          <Route path="/edit-lab/:labName" element={<EditLabComponent/>}></Route> 
          <Route path="/bulk-add-lab" element={
              isAdmin() ? <BulkAddLabComponent/> : <NotAuthorized/>
          }></Route>
            <Route path="/bulk-add-user" element={
                isAdmin() ? <BulkAddUserComponent/> : <NotAuthorized/>
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
            <Route path='/dashboard' element={<AccountDashboard/>}></Route>
          <Route path="*" element={<NotFound/>}></Route>          
        </Routes>
      </div>   
      <Footer />
      </Router>
    </div>
  );
}

export default App;
