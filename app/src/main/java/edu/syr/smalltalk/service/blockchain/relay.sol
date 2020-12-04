// SPDX-License-Identifier: GPL-3.0
pragma solidity ^0.5.16;

contract Relay {
    // according to style guide

    // Type declarations
    struct User {
        bytes32 publicKeyLeft;
        bytes32 publicKeyRight;
        bytes32 name;
        bytes32 avatarUrl;
        uint256 messageStartBlock;
        bool isRegistered;
    }

    // struct Group {
    //     string groupName;
    //     string
    // }

    // enum RelationshipType {NoRelation, Requested, Connected, Blocked}

    // State variables
    mapping(address => User) public users;
    // mapping(address => mapping(address => RelationshipType)) relationships;

    // Events
    event registerSuccessEvent(address indexed who);
    event messageSentEvent(
        address indexed from,
        address indexed to,
        string  message
    );
    // event addContactEvent(address indexed from, address indexed to);
    // event acceptContactEvent(address indexed from, address indexed to);
    // event profileUpdateEvent(address indexed from, bytes32 name, bytes32 avatarUrl);
    // event blockContactEvent(address indexed from, address indexed to);
    // event unblockContactEvent(address indexed from, address indexed to);
    //    event messageEvent(bytes32 memory from, bytes32 memory to, string memory message);
    // event messageGroupEvent(bytes32 from, uint8 groupId, string message);

    // Functions
    modifier checkRegister() {
        require(
            users[msg.sender].isRegistered == true,
            "sender account is not a registered user"
        );
        _;
    }

    function register(bytes32 publicKeyLeft, bytes32 publicKeyRight) public {
        require(
            users[msg.sender].isRegistered == false,
            "sender account is already a registered user"
        );

        User memory newMember = User(
            publicKeyLeft,
            publicKeyRight,
            "",
            "",
            0,
            true
        );
        users[msg.sender] = newMember;
        emit registerSuccessEvent(msg.sender);
    }

    // bytes is compact byte[]
    // You should use bytes over byte[] because it is cheaper, since byte[] adds 31 padding bytes between the elements. As a general rule, use bytes for arbitrary-length raw byte data and string for arbitrary-length string (UTF-8) data.
    function sendMessage(address to, string memory message)
    public
    checkRegister
    {
        // TODO: add relationship check
        // require(relationships[to][msg.sender] == RelationshipType.Connected);

        if (users[to].messageStartBlock == 0) {
            users[to].messageStartBlock = block.number;
        }

        emit messageSentEvent(msg.sender, to, message);
    }

    // function blockMessagesFrom(address from) public checkRegister {
    //     require(relationships[msg.sender][from] == RelationshipType.Connected);

    //     relationships[msg.sender][from] = RelationshipType.Blocked;
    //     emit blockContactEvent(msg.sender, from);
    // }

    // function unblockMessagesFrom(address from) public checkRegister {
    //     require(relationships[msg.sender][from] == RelationshipType.Blocked);

    //     relationships[msg.sender][from] = RelationshipType.Connected;
    //     emit unblockContactEvent(msg.sender, from);
    // }

    // function updateProfile(bytes32 name, bytes32 avatarUrl)
    //     public
    //     checkRegister
    // {
    //     users[msg.sender].name = name;
    //     users[msg.sender].avatarUrl = avatarUrl;
    //     emit profileUpdateEvent(msg.sender, name, avatarUrl);
    // }

    // function getRelationWith(address a) public view checkRegister returns (RelationshipType) {
    //     return relationships[msg.sender][a];
    // }

    // function addContact(address addr) public checkRegister {
    //     require(relationships[msg.sender][addr] == RelationshipType.NoRelation);
    //     require(relationships[addr][msg.sender] == RelationshipType.NoRelation);

    //     relationships[msg.sender][addr] = RelationshipType.Requested;
    //     emit addContactEvent(msg.sender, addr);
    // }

    // function acceptContactRequest(address addr) public checkRegister {
    //     require(relationships[addr][msg.sender] == RelationshipType.Requested);

    //     relationships[msg.sender][addr] = RelationshipType.Connected;
    //     relationships[addr][msg.sender] = RelationshipType.Connected;

    //     emit acceptContactEvent(msg.sender, addr);
    // }
}
