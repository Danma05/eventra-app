import { registerUser, loginUser } from "../services/authService.js";

export const register = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ message: "Faltan datos" });
    }

    const user = await registerUser(email, password);

    res.status(201).json({
      message: "Usuario registrado",
      user,
    });
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

export const login = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ message: "Faltan datos" });
    }

    const data = await loginUser(email, password);

    res.json(data);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};
