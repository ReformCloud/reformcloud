package systems.reformcloud.reformcloud2.web.commands;

import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Trio;
import systems.reformcloud.reformcloud2.web.tokens.TokenWebServerAuth;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class WebCommand extends GlobalCommand {

    private static Trio<Long, TokenWebServerAuth, WebRequester> waiting;

    public WebCommand() {
        super("web", "reformcloud.command.web", "The default web command", new ArrayList<>());
    }

    public boolean tryAwait(long time, TokenWebServerAuth auth, WebRequester requester) {
        if (waiting != null) {
            return false;
        }

        waiting = new Trio<>(time, auth, requester);
        return true;
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("verify")) {
            if (waiting == null) {
                return true;
            }

            if (waiting.getFirst() + TimeUnit.SECONDS.toMillis(15) <= System.currentTimeMillis()) {
                return true;
            }

            Objects.requireNonNull(waiting.getSecond()).complete(waiting.getFirst(), waiting.getThird());
        }

        return true;
    }
}
