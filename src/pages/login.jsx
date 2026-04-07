const handleLogin = async () => {
  await authService.login(email, password);
  navigate("/home");
};