from flask import Flask
from bin.ServerLobby import ServerLobby

app = Flask(__name__)


@app.route('/server/lobbies/<maxPlayers>', methods=['GET'])
def lobbies_matching_client_filter(maxPlayers):
    return 'Hello World!' + str(maxPlayers)


@app.route('/server/lobby/<id>', methods=['DELETE'])
def match_started_lobby_deleted(id):
    return 'Hello World!'


@app.route('/server/lobbies', methods=['POST'])
def create_new_lobby():
    #get data from body of request
    add_lobby_to_dict()
    return 'Hello World!'


@app.route('/server/lobby/<id>/numberOfPlayer', methods=['PUT'])
def exit_lobby_decrement_number_of_players(id):
    return 'Hello World!'


@app.route('/server/lobby/<id>/managerClientIp', methods=['PUT'])
def update_manager_client_info(id):
    return 'Hello World!'


@app.route('/server/lobby', methods=['GET'])
def connect_to_lobby():
    return 'Hello World!'


def add_lobby_to_dict():
    global count
    global temporary_dict_of_lobbies
    temporary_dict_of_lobbies[count] = ServerLobby('a', 'a', 'b', 'v', '1')
    count += 1


if __name__ == '__main__':
    temporary_dict_of_lobbies = {}
    count = 0
    app.run()

