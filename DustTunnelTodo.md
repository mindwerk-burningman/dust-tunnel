# DustTunnelTodo #

<!-- * processing -->
<!-- * processing in DustTunnel -->
<!-- * osc listening -->
<!-- * osc to midi -->
<!-- * midi notes to logic -->
<!-- * osc to midi cc -->
<!-- * midi cc to logic -->
<!-- * assign cc messages to controls/automation -->
<!-- * headset to muze io -->
<!-- * headset to DustTunnel -->
<!-- * headset to DustTunnel to make a sound -->
<!-- * configure muze io -->
<!-- * record and playback - http://developer.choosemuse.com/research-tools/museplayer -->
* start programs from sh script

## Music ##

<!-- * multiple tracks/instruments with controls -->
<!-- * random midi notes separate tracks -->
<!-- * create three tracks -->
  <!-- - low droney sounds
    + drops octaves
    + really long release -->
  <!-- - pad swells -->
    <!-- + mid range -->
    <!-- + uses chords? -->
  <!-- - sparkles -->
    <!-- + raises octaves -->
    <!-- + short ASDR -->
    <!-- + lots of reverb -->
<!-- * hard limiter/compressor on master -->


## DustTunnel ##

<!-- * inc_dec for note engine probabilities -->
  <!-- - implement based on model is increasing? -->
* connecting CC messages
  - pass model to them
  - may be solved with model updates
  - may want to do percentage between min/max
* bug with losing connections and osc complaining...
<!-- * inc/dec methods of changing model values instead of within range -->
  <!-- - figure out amount of which to do so -->
<!-- * limit note engine to certain bands -->
<!-- * `NoteFactory` -->
<!-- * `ControlChangeFactory` -->
  <!-- - update ranges -->
<!-- * random note selection from arrays of dorian scales -->
<!-- * random duration/velocity -->
<!-- * chord arrays -->
<!-- * scale arrays -->
<!-- * stopped signal (change person or takes off) -->
  <!-- - fade back to default state -->
  <!-- - change root note and scale -->

## trigger actions ##
* bass
  - cc 9 => 100 until attention drops below threshold
  - cc 8 => 10 => 64
  - velocity = 60 - 100
* sparkles
  - cc 3 = delay time L / high => low
  - cc 4 = delay time R / high => low
* pad
  - cc 5 = lfo rate 0 - 127 and back
  - cc 6 = high pass 0 - 100

## default states
* bass
  - channel 1
  - cc 9 freq = 0 - 25
  - velocity 0 - 40
  - much shorter duration
* sparkles
  - channel 2
  - velocity = 0
  - cc 1 = dry for stereo verb
  - cc 2 = reverb time
* pad
  - channel 3
  - min/max vel???
  - cc 7 = glide time 0 - 64

## MindWave ##

`/sean/alpha 0.285145`
`/sean/beta 0.285145`
`/sean/gamma 0.285145`
&&
`/sean/alphaavg 0.285145`
`/sean/betaavg 0.285145`

```sh
ssh pi@10.0.1.5 # password is pi
cat /etc/rc.local # startup script

~/pi/run/srv3.py.mindwerk # the one I want

# copy script to run on startup
cp srv3.py.mindwerk srv3.py

# manual run
python3 /home/pi/run/srv3.py --leds 250 --ip 10.0.1.5 --port 9000
```

## Commands ##

`muse-io --device Muse-98A9`

sending from device to DustTunnel
`muse-io --device Muse-98A9 --osc osc.udp://localhost:9000`

reading file over TCP
`muse-player -f muselab_recording.muse -s osc.tcp://127.0.0.1:9000`

read file to mindwave
`muse-player -f muselab_recording.muse -s osc.udp://10.0.1.5:9000`

read file to local Max
`muse-player -f muselab_recording.muse -s osc.udp://127.0.0.1:9000`