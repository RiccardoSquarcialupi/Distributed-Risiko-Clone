import unittest

from src.ServerLobby import ServerLobby


class MyTestCase(unittest.TestCase):
    server1 = ServerLobby('server', 0, '0.0.0.0', 3, 1)

    def test_basic_server_lobby(self):
        self.assertEqual('server', self.server1.name)
        self.assertEqual(0, self.server1.lobby_id)
        self.assertEqual('0.0.0.0', self.server1.manager_client_ip)
        self.assertEqual(3, self.server1.max_players)
        self.assertEqual(1, self.server1.players_inside)

    def test_updating_server_lobby_attribute(self):
        # adding players
        self.server1.add_players_inside()
        self.assertEqual(2, self.server1.players_inside)
        self.server1.add_players_inside()
        self.assertEqual(3, self.server1.players_inside)
        with self.assertRaises(ValueError):
            self.server1.add_players_inside()
        self.server1.remove_players_inside()
        self.server1.remove_players_inside()
        self.server1.remove_players_inside()
        self.assertEqual(0, self.server1.players_inside)
        with self.assertRaises(ValueError):
            self.server1.remove_players_inside()


if __name__ == '__main__':
    unittest.main()
