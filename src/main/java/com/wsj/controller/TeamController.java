package com.wsj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wsj.common.BaseResponse;
import com.wsj.common.DeleteRequest;
import com.wsj.common.ErrorCode;
import com.wsj.common.ResultUtils;
import com.wsj.exception.BusinessException;
import com.wsj.model.domain.Team;
import com.wsj.model.domain.User;
import com.wsj.model.domain.UserTeam;
import com.wsj.model.dto.TeamQuery;
import com.wsj.model.request.TeamAddRequest;
import com.wsj.model.request.TeamJoinRequest;
import com.wsj.model.request.TeamQuitRequest;
import com.wsj.model.request.TeamUpdateRequest;
import com.wsj.model.vo.TeamUserVO;
import com.wsj.service.TeamService;
import com.wsj.service.UserService;
import com.wsj.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 队伍接口
 */
@RestController
@RequestMapping("/team")

@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        // 数据为空时抛出异常
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 取出登录用户的信息
        User loginUser = userService.getLoginUser(request);
        // 将信息复制
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        // 将信息录入并返回队伍的Id
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        // 数据为空时抛出异常
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 取出登录用户的信息
        User loginUser = userService.getLoginUser(request);
        // 更新队伍信息
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        // 数据不合法，抛出异常
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据id获取队伍信息
        Team team = teamService.getById(id);
        // 找不到该队我就抛出异常
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        // 数据为空时，抛出异常
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断用户是否为管理员
//        boolean isAdmin = userService.isAdmin(request);
        boolean isAdmin = true;
        // 根据条件查出的队伍信息存入 teamList 中
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        // 1、查询队伍列表
        /* 简单来说，就是把teamList中每个元素的id提取出来，放到一个新的List中。*/
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        // 2、找出这个用户所在的队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            // 获取当前用户的信息
            User loginUser = userService.getLoginUser(request);
            // 添加查询条件：找出这个用户所在的队伍
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            // 从userTeamList取出队伍id，并设置为set
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            // 遍历 teamList
            teamList.forEach(team -> {
                // 判断 teamList 中的队伍是否被包含在 hasJoinTeamIdSet 中
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception ignored){}
        // 3、查询已加入队伍的人数
        // 根据队伍的id列表，查出数据并存到 userTeamList
//        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
//        userTeamJoinQueryWrapper.in("teamId", teamIdList);
//        List<UserTeam> userTeamList = userTeamService.list(userTeamJoinQueryWrapper);

        // 队伍 id => 加入这个队伍的用户列表
        /*将userTeamList这个列表转换成一个流，然后根据UserTeam对象的teamId属性进行分组，
        将每个teamId对应的UserTeam对象列表收集到一个Map中，
        其中teamId作为键，UserTeam对象列表作为值。
        这样就可以方便地根据teamId查找对应的UserTeam对象列表了。*/
//        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream()
//                .collect(Collectors.groupingBy(UserTeam::getTeamId));

        // 知道每个team在 UserTeam 中出现了多少次，就有多少人在队伍里
        /*使用了List的forEach方法和Map的getOrDefault方法。这个方法可以对列表中的每个元素执行一个操作，
        这里的操作是调用team对象的setHasJoinNum方法，设置team的hasJoinNum属性。
        这个属性的值是从teamIdUserTeamList这个Map中根据team的id属性获取对应的UserTeam对象列表的大小，
        如果Map中没有这个键，就返回一个空的列表，并取其大小，即0。
        这样就可以统计每个team有多少个UserTeam对象了。*/
//        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>())
//                .size()));

        // 根据队伍公开或加密来返回队伍数据
        return ResultUtils.success(teamList);
    }

//    // todo 查询分页
//    @GetMapping("/list/page")
//    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
//        if (teamQuery == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Team team = new Team();
//        BeanUtils.copyProperties(teamQuery, team);
//        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
//        Page<Team> resultPage = teamService.page(page, queryWrapper);
//        return ResultUtils.success(resultPage);
//    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        // 数据为空时，抛出异常
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取当前用户的信息
        User loginUser = userService.getLoginUser(request);
        // 执行加入队伍的操作
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(true);
    }


    /**
     * 获取我创建的队伍
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }
    /**
     * 获取我加入的队伍
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        // 数据为空时，抛出异常
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 找到该用户加入的所有队伍的id
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 如果该用户没有加入队伍，则直接返回空的数据
        if (CollectionUtils.isEmpty(userTeamList)) {
            return ResultUtils.success(new ArrayList<>());
        }
        // （去重）取出不重复的队伍 id
        // teamId userId
        // 1, 2
        // 1, 3
        // 2, 3
        // result
        // 1 => 2, 3
        // 2 => 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        // 根据id找出队伍的详细信息
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        for (TeamUserVO team : teamList) {
            team.setHasJoin(true);
        }
//        teamList.forEach(team -> {
//            team.setHasJoin(true);
//        });
        return ResultUtils.success(teamList);
    }

    // 找出在队伍中的用户
    @GetMapping("/list/usersInTeam")
    public BaseResponse<List<User>> listUsersInTeam(@RequestParam int teamId) {
        // 在表user_team中查出这个队伍的用户
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        queryWrapper.eq("isDelete",0);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 提取id，作为新的list
        final List<Long> userIdList = userTeamList.stream().map(UserTeam::getUserId)
                .collect(Collectors.toList());

        // 在表user中查询这些用户的详细信息
        QueryWrapper<User> queryWrapperUser = new QueryWrapper<>();
        queryWrapperUser.in("id",userIdList);
        List<User> userList = userService.list(queryWrapperUser);
        // 用户信息脱敏
        List<User> safeUserList = userList.stream().map(user -> userService.getSafetyUser(user))
                .collect(Collectors.toList());

        return ResultUtils.success(safeUserList);
    }
}
