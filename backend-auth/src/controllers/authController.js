import { registerUser, loginUser } from "../services/authService.js";

export const register = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        message: "Correo y contraseña son obligatorios"
      });
    }

    const user = await registerUser(email, password);

    return res.status(201).json({
      message: "Usuario registrado correctamente",
      user
    });

  } catch (error) {
    return res.status(400).json({
      message: error.message
    });
  }
};

export const login = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        message: "Correo y contraseña son obligatorios"
      });
    }

    const data = await loginUser(email, password);

    return res.status(200).json(data);

  } catch (error) {
    return res.status(400).json({
      message: error.message
    });
  }
};