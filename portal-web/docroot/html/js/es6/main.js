import {log as carlosLog} from 'html/js/es6/a/carlos';
import {log as chemaLog} from 'html/js/es6/b/chema';

function log(text) {
	carlosLog(text);
}

export {chemaLog as log};