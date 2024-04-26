package ch.uzh.ifi.hase.soprafs24.endpoint.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.user.User;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.CredentialsDTO;
import ch.uzh.ifi.hase.soprafs24.endpoint.rest.dto.GameSettingsDTO;
import ch.uzh.ifi.hase.soprafs24.game.View.SettingView;
import ch.uzh.ifi.hase.soprafs24.game.entity.Settings;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "token", target = "token")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "maxPlayers", target = "maxPlayers")
  @Mapping(source = "rounds", target = "rounds")
  @Mapping(source = "guessingTime", target = "guessingTime")
  // @Mapping(source = "host", target = "hostPlayer")
  Settings convertGameSettingsDTOToEntity(GameSettingsDTO gameSettingsDTO);

  Settings gameSettingsDTOtoSettings(GameSettingsDTO settingsDTO);

  User convertCredentialsDTOtoUser(CredentialsDTO credentials);

  GameSettingsDTO convertSettingsToDTO(SettingView settings);

  User convertSettingsDTOtoUser(GameSettingsDTO settingsDTO);

  CredentialsDTO convertSettingsDTOtoCredentialsDTO(GameSettingsDTO settingsDTO);
}
