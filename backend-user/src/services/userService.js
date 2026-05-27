const {
  createUserProfile,
  findProfileByAuthUserId,
  findProfileByUsername,
  updateUserProfile,
} = require("../models/userModel");

const registerProfile = async (authUserId, profileData) => {
  const existingByAuthId = await findProfileByAuthUserId(authUserId);

  if (existingByAuthId) {
    const error = new Error("El usuario ya tiene un perfil creado");
    error.statusCode = 409;
    throw error;
  }

  const existingByUsername = await findProfileByUsername(profileData.username);

  if (existingByUsername) {
    const error = new Error("El username ya está en uso");
    error.statusCode = 409;
    throw error;
  }

  return await createUserProfile({
    auth_user_id: authUserId,
    username: profileData.username,
    first_name: profileData.first_name,
    last_name: profileData.last_name,
    phone: profileData.phone,
    birth_date: profileData.birth_date,
    gender: profileData.gender,
    city: profileData.city,
    country: profileData.country,
    bio: profileData.bio,
    profile_image_url: profileData.profile_image_url,
  });
};

const getProfileByAuthUserId = async (authUserId) => {
  const profile = await findProfileByAuthUserId(authUserId);

  if (!profile) {
    const error = new Error("Perfil no encontrado");
    error.statusCode = 404;
    throw error;
  }

  return profile;
};

const updateProfileByAuthUserId = async (authUserId, profileData) => {
  const currentProfile = await findProfileByAuthUserId(authUserId);

  if (!currentProfile) {
    const error = new Error("Perfil no encontrado");
    error.statusCode = 404;
    throw error;
  }

  if (!profileData.username || !profileData.first_name || !profileData.last_name) {
    const error = new Error("username, first_name y last_name son obligatorios");
    error.statusCode = 400;
    throw error;
  }

  if (profileData.username !== currentProfile.username) {
    const existingByUsername = await findProfileByUsername(profileData.username);

    if (existingByUsername) {
      const error = new Error("El username ya está en uso");
      error.statusCode = 409;
      throw error;
    }
  }

  return await updateUserProfile(authUserId, profileData);
};

module.exports = {
  registerProfile,
  getProfileByAuthUserId,
  updateProfileByAuthUserId,
};