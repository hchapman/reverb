#include "logging.h"

void dlog(int level, const char *fmt, ...) {
	va_list args;

	va_start(args, fmt);
	__android_log_vprint(ANDROID_LOG_DEBUG, "Reverb", fmt, args);
	va_end(args);
}
