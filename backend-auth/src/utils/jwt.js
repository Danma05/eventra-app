import jwt from "jsonwebtoken";

export const generateToken = (user) => {
  return jwt.sign(
    {
      id: user.id,
      email: user.email,
      account_type: user.account_type
    },
    process.env.JWT_SECRET,
    { expiresIn: "1h" }
  );
};