import { Navigate } from "react-router-dom";
import authService from "../services/authService";

const ProtectedRoute = ({ children }) => {
  if (!authService.isAuthenticated()) {
    navigate("/login");
  }
  return children;
};

export default ProtectedRoute;