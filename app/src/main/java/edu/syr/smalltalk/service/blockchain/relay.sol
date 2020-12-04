// SPDX-License-Identifier: GPL-3.0
pragma solidity ^0.5.16;

contract Relay {

    // event messageSentEvent(address indexed from, address indexed to, bytes message, bytes32 encryption);
    event addContactEvent(address indexed from, address indexed to);
    // event acceptContactEvent(address indexed from, address indexed to);
    // event profileUpdateEvent(address indexed from, bytes32 name, bytes32 avatarUrl);
    // event blockContactEvent(address indexed from, address indexed to);
    // event unblockContactEvent(address indexed from, address indexed to);

    event testEvent(string param1, uint32 param2);
    event messageEvent(bytes32 from, bytes32 to, string memory message);
    event messageGroupEvent(bytes32 from, uint8 groupId, string memory message);

    enum RelationshipType {NoRelation, Requested, Connected, Blocked}

    struct User {
        bytes32 publicKeyLeft;
        bytes32 publicKeyRight;
        bytes32 name;
        bytes32 avatarUrl;
        uint messageStartBlock;
        bool isMember;
    }

    struct Group {
        string groupName;
        string
    }

    function test(string memory param1, uint32 param2) public {
        emit testEvent(param1, param2);
    }

    mapping (address => mapping (address => RelationshipType)) relationships;
    mapping (address => User) public users;

    function addContact(address addr) public onlyMember {
         require(relationships[msg.sender][addr] == RelationshipType.NoRelation);
         require(relationships[addr][msg.sender] == RelationshipType.NoRelation);

         relationships[msg.sender][addr] = RelationshipType.Requested;
         emit addContactEvent(msg.sender, addr);
    }

    function acceptContactRequest(address addr) public onlyMember {
         require(relationships[addr][msg.sender] == RelationshipType.Requested);

         relationships[msg.sender][addr] = RelationshipType.Connected;
         relationships[addr][msg.sender] = RelationshipType.Connected;

         emit acceptContactEvent(msg.sender, addr);
    }

    // function join(bytes32 publicKeyLeft, bytes32 publicKeyRight) public {
    //     require(members[msg.sender].isMember == false);

    //     User memory newMember = User(publicKeyLeft, publicKeyRight, "", "", 0, true);
    //     members[msg.sender] = newMember;
    // }

    // function sendMessage(address to, bytes message, bytes32 encryption) public onlyMember {
    //     require(relationships[to][msg.sender] == RelationshipType.Connected);

    //     if (members[to].messageStartBlock == 0) {
    //         members[to].messageStartBlock = block.number;
    //     }

    //     emit messageSentEvent(msg.sender, to, message, encryption);
    // }

    // function blockMessagesFrom(address from) public onlyMember {
    //     require(relationships[msg.sender][from] == RelationshipType.Connected);

    //     relationships[msg.sender][from] = RelationshipType.Blocked;
    //     emit blockContactEvent(msg.sender, from);
    // }

    // function unblockMessagesFrom(address from) public onlyMember {
    //     require(relationships[msg.sender][from] == RelationshipType.Blocked);

    //     relationships[msg.sender][from] = RelationshipType.Connected;
    //     emit unblockContactEvent(msg.sender, from);
    // }

    // function updateProfile(bytes32 name, bytes32 avatarUrl) public onlyMember {
    //     members[msg.sender].name = name;
    //     members[msg.sender].avatarUrl = avatarUrl;
    //     emit profileUpdateEvent(msg.sender, name, avatarUrl);
    // }

    // modifier onlyMember() {
    //     require(members[msg.sender].isMember == true);
    //     _;
    // }

    // function getRelationWith(address a) public view onlyMember returns (RelationshipType) {
    //     return relationships[msg.sender][a];
    // }
}
