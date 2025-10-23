import * as readline from 'readline';

/**
 * CLI utilities for user interaction
 */

/**
 * Create readline interface
 */
function createReadlineInterface() {
    return readline.createInterface({
        input: process.stdin,
        output: process.stdout,
    });
}

/**
 * Read user input from console
 */
export function getUserInput(prompt: string): Promise<string> {
    const rl = createReadlineInterface();

    return new Promise((resolve) => {
        rl.question(prompt, (answer) => {
            rl.close();
            resolve(answer.trim());
        });
    });
}

/**
 * Interactive chat loop
 */
export async function startInteractiveChatLoop(
    chatHandler: (message: string) => Promise<void>
): Promise<void> {
    console.log('\n💡 Interactive chat mode started. Type "exit" or "quit" to end the conversation.\n');

    let continueChat = true;

    while (continueChat) {
        const userMessage = await getUserInput('\n👤 You: ');
        console.log('');

        // Check for exit commands
        if (userMessage.toLowerCase() === 'exit' || userMessage.toLowerCase() === 'quit') {
            console.log('\n👋 Goodbye!');
            continueChat = false;
            break;
        }

        // Skip empty messages
        if (!userMessage.trim()) {
            continue;
        }

        // Process the message
        try {
            await chatHandler(userMessage);
        } catch (error) {
            console.error('❌ Error processing message:', error);
        }
    }
}

/**
 * Interactive chat loop with streaming support
 */
export async function startInteractiveChatLoopStream(
    chatStreamHandler: (message: string) => AsyncGenerator<string>
): Promise<void> {
    console.log('\n💡 Interactive chat mode started (Streaming). Type "exit" or "quit" to end the conversation.\n');

    let continueChat = true;

    while (continueChat) {
        const userMessage = await getUserInput('\n👤 You: ');
        console.log('');

        // Check for exit commands
        if (userMessage.toLowerCase() === 'exit' || userMessage.toLowerCase() === 'quit') {
            console.log('\n👋 Goodbye!');
            continueChat = false;
            break;
        }

        // Skip empty messages
        if (!userMessage.trim()) {
            continue;
        }

        // Process the message with streaming
        try {
            process.stdout.write('🤖 Assistant: ');
            for await (const chunk of chatStreamHandler(userMessage)) {
                process.stdout.write(chunk);
            }
            console.log('\n');
        } catch (error) {
            console.error('❌ Error processing message:', error);
        }
    }
}

/**
 * Display welcome message
 */
export function displayWelcome(): void {
    console.log('\n' + '='.repeat(60));
    console.log('🤖  MCP Multi-Server Agent');
    console.log('='.repeat(60));
}
