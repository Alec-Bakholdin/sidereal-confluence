package com.bakholdin.siderealconfluence.gameactions;

import com.bakholdin.siderealconfluence.dto.UpdateGameDto;
import com.bakholdin.siderealconfluence.dto.UpdatePlayerDto;
import com.bakholdin.siderealconfluence.dto.UserDto;
import com.bakholdin.siderealconfluence.mapper.UserMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Getter
@ToString
@RequiredArgsConstructor
public class GameActionUpdateContainer {
    private final Long gameId;
    private final UserMapper userMapper;

    private final Map<String, UpdatePlayerDto.Builder> updatePlayerDtoBuilderMap = new HashMap<>();
    private UpdateGameDto.Builder updateGameDtoBuilder;

    public UpdatePlayerDto.Builder fetchPlayerUpdater(UserDto userDto) {
        if (!updatePlayerDtoBuilderMap.containsKey(userDto.getUsername())) {
            updatePlayerDtoBuilderMap.put(
                    userDto.getUsername(),
                    UpdatePlayerDto.builder()
                            .user(userMapper.toUsernameOnly(userDto))
            );
        }
        return updatePlayerDtoBuilderMap.get(userDto.getUsername());
    }

    public List<UpdatePlayerDto> buildUpdatePlayerDtos() {
        return updatePlayerDtoBuilderMap.values().stream()
                .map(UpdatePlayerDto.Builder::build)
                .collect(Collectors.toList());
    }

    public UpdateGameDto.Builder fetchGameUpdater() {
        if (updateGameDtoBuilder == null) {
            updateGameDtoBuilder = UpdateGameDto.builder().id(gameId);
        }
        return updateGameDtoBuilder;
    }

    public UpdateGameDto buildUpdateGameDto() {
        if (updateGameDtoBuilder == null) {
            return null;
        }
        return updateGameDtoBuilder.build();
    }
}
