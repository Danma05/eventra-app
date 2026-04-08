const {
  createUserProfile,
  findProfileByAuthUserId,
  findProfileByUsername,
} = require("../models/userModel");

const registerProfile = async (authUserId, profileData) => {
  const existingByAuthId = await findProfileByAuthUserId(authUserId);
  if (existingByAuthId) {
    throw new Error("El usuario ya tiene un perfil creado");
  }

  const existingByUsername = await findProfileByUsername(profileData.username);
  if (existingByUsername) {
    throw new Error("El username ya está en uso");
  }

  const profile = await createUserProfile({
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

  return profile;
};

const getProfileByAuthUserId = async (authUserId) => {
  const profile = await findProfileByAuthUserId(authUserId);

  if (!profile) {
    throw new Error("Perfil no encontrado");
  }

  return profile;
};

module.exports = {
  registerProfile,
  getProfileByAuthUserId,
};