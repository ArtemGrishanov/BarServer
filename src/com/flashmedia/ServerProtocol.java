package com.flashmedia;

        public class ServerProtocol
        {
				// client generated messages

                public static final int C_LOAD_BAR = 0x10;

                public static final int C_CLIENT_COME = 0x11;

                public static final int C_CLIENT_SERVED = 0x12;

                public static final int C_CLIENT_DENIED = 0x13;

                public static final int C_PRODUCTION_LICENSED = 0x14;

                public static final int C_PRODUCTION_ADDED_TO_BAR = 0x15;

                public static final int C_PRODUCTION_DELETED = 0x16;

                public static final int C_USER_ATTRS_CHANGED = 0x17;

                public static final int C_MONEY_CENT_CHANGED = 0x18;

                public static final int C_DECOR_ADDED_TO_BAR = 0x19;

                public static final int C_DECOR_DELETED = 0x20;

                public static final int C_ENTER_BY_INVITE = 0x21;

                public static final int C_LOAD_FRIENDS = 0x22;

                public static final int C_LOAD_TOP = 0x23;

                public static final int C_LOAD_BAR_CATALOG = 0x24;

                public static final int C_VK_ATTRS = 0x25;

                public static final int C_MONEY_EURO_CHANGED = 0x26;

                public static final int C_RESET_GAME = 0x27;

                public static final int C_PRODUCTION_CHANGE_PARTS = 0x28;

                public static final int C_PRODUCTION_CHANGE_PLACE = 0x29;

                public static final int C_WITHDRAW_VOTES = 0x30;

                // server generated messages

                public static final int S_FIRST_LAUNCH = 0x50;

                public static final int S_ERROR = 0x51;

                public static final int S_MESSAGE_BOX = 0x52;

                public static final int S_NEWS_LOADED = 0x53;

                public static final int S_LEVEL_CHANGED = 0x54;

                public static final int S_EXP_CHANGED = 0x55;

                public static final int S_LOVE_CHANGED = 0x56;

                public static final int S_MONEY_CENT_CHANGED = 0x57;

                public static final int S_MONEY_EURO_CHANGED = 0x58;

                public static final int S_TOP_LOADED = 0x59;

                public static final int S_FRIENDS_LOADED = 0x60;

                public static final int S_BAR_CATALOG_LOADED = 0x61;

                public static final int S_BAR_LOADED = 0x62;

                public static final int S_WITHDRAW_VOTES_OK = 0x63;

                public static final int S_WITHDRAW_VOTES_NOT_ENOUGH = 0x64;

                public static final int S_WITHDRAW_VOTES_ERROR = 0x65;

                public static final int S_BONUS_FROM_INVITE = 0x66;
        }

