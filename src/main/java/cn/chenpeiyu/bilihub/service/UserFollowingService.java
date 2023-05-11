package cn.chenpeiyu.bilihub.service;
import cn.chenpeiyu.bilihub.dao.UserFollowingDao;
import cn.chenpeiyu.bilihub.domain.FollowingGroup;
import cn.chenpeiyu.bilihub.domain.User;
import cn.chenpeiyu.bilihub.domain.UserFollowing;
import cn.chenpeiyu.bilihub.domain.UserInfo;
import cn.chenpeiyu.bilihub.domain.constant.UserConstant;
import cn.chenpeiyu.bilihub.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Transactional
    public void addUserFollowings(UserFollowing userFollowing) {
        // 判断前端是否将分组的id传入
        Long groupId = userFollowing.getGroupId();
        if(groupId == null){
            //如果传入的分组id为空 说明没传入 放到默认分组当中
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else{
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if(followingGroup == null){
                throw new ConditionException("关注分组不存在！");
            }
        }
        //获取关注的用户
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if(user == null){
            throw new ConditionException("关注的用户不存在！");
        }
//        如果有记录，先删除掉对应的记录 然后再重新添加，以达到更新的作用
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);
    }

    // 第一步：获取关注的用户列表
    // 第二步：根据关注用户的id查询关注用户的基本信息
    // 第三步：将关注用户按关注分组进行分类
    public List<FollowingGroup> getUserFollowings(Long userId){
//        根据id获取对应的关注列表
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
//        将关注列表当中的关注id抽取出来 形成一个关注id的set
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if(followingIdSet.size() > 0){
//            根据关注id获取关注人的信息
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
//       找到每个用户关注对象对应的关注人的用户信息并匹配
        for(UserFollowing userFollowing : list){
            for(UserInfo userInfo : userInfoList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
//        获取和该用户相关的组的列表
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
//        全部关注，不需要存在数据库当中但是前端需要
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
//        获取全部的关注分组
        for(FollowingGroup group : groupList){
            List<UserInfo> infoList = new ArrayList<>();
            for(UserFollowing userFollowing : list){
//              关注分组的一个匹配
                if(group.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }

            }
//           将属于该组的所有的用户信息写到这个组当中去
            group.setFollowingUserInfoList(infoList);

            result.add(group);
        }
        return result;
    }

    // 第一步：获取当前用户的粉丝列表
    // 第二步：根据粉丝的用户id查询基本信息
    // 第三步：查询当前用户是否已经关注该粉丝
    public List<UserFollowing> getUserFans(Long userId){
//        查看谁关注了该用户 对得到对应集合，查询条件：where followingId = userId
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
//        粉丝id集合
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
//        获取粉丝的信息
        List<UserInfo> userInfoList = new ArrayList<>();
        if(fanIdSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
//        将粉丝信息和粉丝进行绑定
        for(UserFollowing fan : fanList){
            for(UserInfo userInfo : userInfoList){
                if(fan.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
//          判断互粉的情况b
            for(UserFollowing following : followingList){
                if(following.getFollowingId().equals(fan.getUserId())){
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    /**
     * 判断关注的情况
     * @param userInfoList 分页查询的用户信息表
     * @param userId 用户id
     * @return
     */
    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
//        获取用户当前关注的用户
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for(UserInfo userInfo : userInfoList){
            userInfo.setFollowed(false);
            for(UserFollowing userFollowing : userFollowingList){
//                如果关注用户对象的id和当前的id相同 说明说已经关注了
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
