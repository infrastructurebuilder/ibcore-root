# ibexecutor

Standard interfaces (and exceptions) for executing processes and capturing the results.

Some help with the complexity of running a set of general processes:

    Logger log; // Some Logger
    Path relativeRoot;  // If set then all Checksum Paths are set "relative" to this making the checksum somewhat portable  See ChecksumBuilder for details
    String EXEC1 = "exec1"; // IDS MUST BE UNIQUE WITHIN A ProcessRunner AND within a relative working path set (we make directories there...)
    String EXEC2 = "exec2";
    String onePath = "/path/to/executable1";
    String twoPath = "/path/to/executable2";
    Checksum onePathCsum = null;  // Set it to the value of the checksum of "onePath" if desired
    boolean optionalExecution = false;
    Path workingDir = blah; // Some working path, which if presnent we will cd to
    Map<String,String> envParams = new HashMap<>();
    Optional<Path> stdIn = Optional.empty(); // No input
    List<Integer> validExits = Arrays.asList(0,-1);
    List<String> oneParams =Arrays.asList("params","to","pass","to","executable");
    List<String> twoParams =Arrays.asList("paramz","tooo","pazz","2","executable2");
    boolean background = false;

    ProcessRunner pr = new ProcessRunner(scratchDir, Optional.of(System.out), Optional.of(logger), Optional.of(relativeRoot))
        .addExecution(EXEC1, onePath, oneParams, Optional.of(Duration.ofSeconds(20)), stdIn,
          Optional.of(workingDir), Optional.ofNullable(onePathCsum), optionalExecution, Optional.of(envParams), Optional.of(relativeRoot),
          Optional.of (validExits),background)
        .addExecution(EXEC2, twoPath, twoParams, Optional.empty(), Optional.empty(),
          Optional.of(workingDir), Optional.empty() /* no checksum here */, false, Optional.of(envParams), Optional.of(relativeRoot),
          Optional.empty() /* ZERO is the only valid success */,false)
          //  Here's how the processes get started
          // The second param is the amout of time to sleep between iterations of nicely-kill and forcibly-kill for ill-tempered background processes
          // Optional.of(0L) is equivalent to Optional.empty()
          .lock(maxWaitTime, Optional.of(0L));

    ProcessExecutionResultBag exec1Proc = pr.get().get();

    ProcessExecutionResult res1 = exec1Proc.getResults().get(EXEC1); // Results for EXEC1 run
    if (res1.isError()) {
      throw new RuntimeException("Failed to get 'exec1' result",
          res1.getException().orElse(new RuntimeException("encapsulated")));
    }

