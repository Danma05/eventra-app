useEffect(() => {
  const token = authService.getToken();
  if (token) {
    navigate("/home");
  }
}, []);

const handleLogout = () => {
  authService.logout();
  navigate("/login");
};