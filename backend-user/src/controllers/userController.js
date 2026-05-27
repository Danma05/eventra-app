const {
  registerProfile,
  getProfileByAuthUserId,
  updateProfileByAuthUserId,
} = require("../services/userService");

const createProfile = async (req, res) => {
  try {
    const authUserId = req.user.id;
    const { username, first_name, last_name } = req.body;

    if (!username || !first_name || !last_name) {
      return res.status(400).json({
        message: "username, first_name y last_name son obligatorios",
      });
    }

    const profile = await registerProfile(authUserId, req.body);

    return res.status(201).json({
      message: "Perfil creado correctamente",
      profile,
    });
  } catch (error) {
    return res.status(error.statusCode || 400).json({
      message: error.message,
    });
  }
};

const getMyProfile = async (req, res) => {
  try {
    const authUserId = req.user.id;
    const profile = await getProfileByAuthUserId(authUserId);

    return res.status(200).json(profile);
  } catch (error) {
    return res.status(error.statusCode || 404).json({
      message: error.message,
    });
  }
};

const updateMyProfile = async (req, res) => {
  try {
    const authUserId = req.user.id;

    const profile = await updateProfileByAuthUserId(authUserId, req.body);

    return res.status(200).json({
      message: "Perfil actualizado correctamente",
      profile,
    });
  } catch (error) {
    return res.status(error.statusCode || 500).json({
      message: error.message,
    });
  }
};

const getProfileByAuthId = async (req, res) => {
  try {
    const authUserId = parseInt(req.params.authUserId, 10);
    const profile = await getProfileByAuthUserId(authUserId);

    return res.status(200).json(profile);
  } catch (error) {
    return res.status(error.statusCode || 404).json({
      message: error.message,
    });
  }
};

module.exports = {
  createProfile,
  getMyProfile,
  updateMyProfile,
  getProfileByAuthId,
};