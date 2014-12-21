function refresh() {
    $.getJSON('ajax.php', function (data) {
	var html = "";

        for (id in data) {
        	var percent = (20-data[id]['tps'])*5;
		var rounded_percent = Math.round(percent);

		if (rounded_percent !== 0) {
			percent = (percent < rounded_percent) ? '<' + rounded_percent : (percent > rounded_percent) ? '>' + rounded_percent : rounded_percent;
		} else { percent = rounded_percent; }

		var type;
        	if (data[id]['status'] == 'down') {
        		type = "down";
        	} else if (data[id]['tps'] < 10) {
				type = "low";
        	} else if (data[id]['tps'] < 15) {
				type = "normal";
        	} else {
				type = "high";
	    	}
	    	
	    	html += '<li class="TPS-' + type + '"><span class="TPS-class"><p><b>' + id + '</b></p><p>TPS: ' + data[id]['tps'] + '</p><p>Players: ' + data[id]['players'] + '</p><p>'
	    	html += (type!='down') ? 'Lag: ' + percent + '%' : 'DOWN';
	    	html += '</p></span></li>';
	    
	    }

        $('#instances').html(html);
	return data;

    });
}

$(document).ready(function () {
	refresh();
	window.setInterval(refresh, 2000);
});
