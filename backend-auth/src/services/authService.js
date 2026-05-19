import bcrypt from "bcrypt";
import {
  findUserByEmail,
  createUser,
  updateLastLogin,
  incrementFailedLoginAttempts
} from "../models/authModel.js";
import { generateToken } from "../utils/jwt.js";

export const registerUser = async (email, password, accountType = "RUNNER") => {
  const existingUser = await findUserByEmail(email);

  if (existingUser) {
    throw new Error("El correo ya está registrado");
  }

  if (!["RUNNER", "ORGANIZER"].includes(accountType)) {
    throw new Error("Tipo de cuenta inválido");
  }

  const hashedPassword = await bcrypt.hash(password, 10);
  const user = await createUser(email, hashedPassword, accountType);

  return user;
};

export const loginUser = async (email, password) => {
  const user = await findUserByEmail(email);

  if (!user) {
    throw new Error("Usuario no encontrado");
  }

  if (user.status !== "ACTIVE") {
    throw new Error("La cuenta no está activa");
  }

  const isMatch = await bcrypt.compare(password, user.password_hash);

  if (!isMatch) {
    const attempts = (user.failed_login_attempts || 0) + 1;
    await incrementFailedLoginAttempts(user.id, attempts);
    throw new Error("Contraseña incorrecta");
  }

  await updateLastLogin(user.id);

  const token = generateToken(user);

  return {
    token,
    user: {
      id: user.id,
      email: user.email,
      account_type: user.account_type,
      status: user.status,
      isVerified: user.is_verified
    }
  };
};