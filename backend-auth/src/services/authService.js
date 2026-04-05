import bcrypt from "bcrypt";
import { findUserByEmail, createUser } from "../models/authModel.js";
import { generateToken } from "../utils/jwt.js";

export const registerUser = async (email, password) => {
  const existingUser = await findUserByEmail(email);

  if (existingUser) {
    throw new Error("El correo ya está registrado");
  }

  const hashedPassword = await bcrypt.hash(password, 10);

  const newUser = await createUser(email, hashedPassword);

  return newUser;
};

export const loginUser = async (email, password) => {
  const user = await findUserByEmail(email);

  if (!user) {
    throw new Error("Usuario no encontrado");
  }

  const isMatch = await bcrypt.compare(password, user.password_hash);

  if (!isMatch) {
    throw new Error("Contraseña incorrecta");
  }

  const token = generateToken(user);

  return {
    token,
    user: {
      id: user.id,
      email: user.email,
    },
  };
};