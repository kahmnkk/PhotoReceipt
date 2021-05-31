// Common
const errors = require('@src/errors');
const dbMgr = require('@src/database/dbMgr');

// Utils
const utils = require('@src/utils/utils');

// Model
const Board = require('@src/model/board');
const User = require('@src/model/user');

class RouterUser {
    constructor() {}

    async getList(reqDto) {
        const reqKeys = {};
        const resKeys = {
            list: 'list',
        };
        let rtn = {};

        if (utils.hasKeys(reqKeys, reqDto) == false) {
            throw utils.errorHandling(errors.invalidRequestData);
        }

        const board = new Board();
        const boardList = [];

        const getBoardRanking = await board.getBoardRankingList();
        for (let i in getBoardRanking) {
            if (i % 2 == 0) {
                // idx
                const boardInfo = await board.getBoardInfo(getBoardRanking[i]);
                boardList.push(boardInfo);
            } else {
                // like count
                boardList[boardList.length - 1].like = Number(getBoardRanking[i]);
            }
        }

        rtn[resKeys.list] = boardList;

        return rtn;
    }

    async getDetail(reqDto) {
        const reqKeys = {
            idx: 'idx',
            userIdx: 'userIdx',
        };
        const resKeys = {
            boardInfo: 'boardInfo',
            ownerInfo: 'ownerInfo',
            isLiked: 'isLiked',
        };
        let rtn = {};

        if (utils.hasKeys(reqKeys, reqDto) == false) {
            throw utils.errorHandling(errors.invalidRequestData);
        }

        const idx = reqDto[reqKeys.idx];
        const userIdx = reqDto[reqKeys.userIdx];

        const board = new Board(idx);
        const boardInfo = await board.getBoardInfo(idx);
        const boardLikeInfo = await board.getBoardLikeInfo(idx);

        boardInfo.like = await board.getBoardLikeCount();

        let isLiked = false;
        for (let i in boardLikeInfo) {
            if (boardLikeInfo[i].userIdx == userIdx) {
                if (boardLikeInfo[i].status == board.likeStatus.like) {
                    isLiked = true;
                }
                break;
            }
        }

        const user = new User(boardInfo.owner);
        const ownerInfo = await user.getUserInfo();

        rtn[resKeys.boardInfo] = boardInfo;
        rtn[resKeys.ownerInfo] = ownerInfo;
        rtn[resKeys.isLiked] = isLiked;

        return rtn;
    }

    async like(reqDto) {
        const reqKeys = {
            idx: 'idx',
            userIdx: 'userIdx',
        };
        const resKeys = {
            isLiked: 'isLiked',
            likeCount: 'likeCount',
        };
        let rtn = {};

        if (utils.hasKeys(reqKeys, reqDto) == false) {
            throw utils.errorHandling(errors.invalidRequestData);
        }

        const idx = reqDto[reqKeys.idx];
        const userIdx = reqDto[reqKeys.userIdx];

        const board = new Board(idx);
        const boardLikeInfo = await board.getBoardLikeInfo(idx);

        let isLiked = false;
        for (let i in boardLikeInfo) {
            if (boardLikeInfo[i].userIdx == userIdx) {
                if (boardLikeInfo[i].status == board.likeStatus.like) {
                    isLiked = true;
                }
                break;
            }
        }

        const likeCount = await board.getBoardLikeCount();
        let rtnLikeCount = likeCount;
        let likeStatus = 0;
        if (isLiked == true) {
            likeStatus = board.likeStatus.unlike;
            rtnLikeCount--;
        } else {
            likeStatus = board.likeStatus.like;
            rtnLikeCount++;
        }
        const createResult = await board.createBoardLike(userIdx, likeStatus, likeCount);

        await dbMgr.set(dbMgr.mysqlConn.master, createResult.query);

        rtn[resKeys.isLiked] = !isLiked;
        rtn[resKeys.likeCount] = rtnLikeCount;

        return rtn;
    }
}

module.exports = new RouterUser();
