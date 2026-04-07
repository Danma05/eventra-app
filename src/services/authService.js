const authService = {
  login: async (email, password) => {
    // Simulación por ahora
    const fakeToken = "fake-jwt-token-123";
    localStorage.setItem("token", fakeToken);
    //esto se debe cambiar por el token del back de yisus y quedar asi:
    //return response.token
    return fakeToken;
  },

  logout: () => {
    localStorage.removeItem("token");
  },

  getToken: () => {
    return localStorage.getItem("token");
  },

  isAuthenticated: () => {
    return !!localStorage.getItem("token");
  }
};

export default authService;