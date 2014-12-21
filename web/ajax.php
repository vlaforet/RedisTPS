<?php
require 'Predis/Autoloader.php';
$data = array();

Predis\Autoloader::register();
$client = new Predis\Client([
    'scheme' => 'tcp',
    'host'   => '127.0.0.1',
    'port'   =>  6379,
    'password'   => 'PassWordRedisServer',

]);

foreach ($client->hkeys('RedisTPS_heartbeats') as $id) {
	$data[$id]['tps'] = $client->hget('RedisTPS_TPS', $id);
	$data[$id]['players'] = $client->hget('RedisTPS_Players', $id);
}

foreach ($client->hkeys('RedisTPS_TPS') as $id) {
	if ($data[$id] === null) {
		$data[$id]['tps'] = $client->hget('RedisTPS_TPS', $id);
		$data[$id]['players'] = $client->hget('RedisTPS_Players', $id);
		$data[$id]['status'] = 'down';
	}
}

echo json_encode($data);

?>
