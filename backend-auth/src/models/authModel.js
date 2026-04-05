import pool from "../config/db.js";

export const findUserByEmail = async (email) => {
  const result = await pool.query(
    "SELECT * FROM users_auth WHERE email = $1",
    [email]
  );
  return result.rows[0];
};

export const createUser = async (email, passwordHash) => {
  const result = await pool.query(
    "INSERT INTO users_auth (email, password_hash) VALUES ($1, $2) RETURNING id, email",
    [email, passwordHash]
  );
  return result.rows[0];
};