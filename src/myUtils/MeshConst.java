package myUtils;

public interface MeshConst {
// Commands for constructing meshes from script (Mesh::read(..))
  static final int MCONST = 19651031;
  static final int
  // Edge highlight styles
  EH_NONE = 0,
  EH_MINOR = 1,
  EH_MAJOR = 2,
  // Vertex highlight styles
  VH_NONE = 0,
  VH_MINOR = 1,
  VH_MAJOR = 2,

      // Renderable types (see Renderable):
      RENDERABLE_FACE = 0,
      RENDERABLE_VERTEX = 1,
      RENDERABLE_LINE = 2,
      RENDERABLE_CIRCLE = 3,
      RENDERABLE_DELETED = 99,

      FPS = 30,
      TICK = FPS / 15,
      // Mesh animation codes

      ANIM_HLVERT1 = 0,
      ANIM_HLVERT1OFF = 1,
      ANIM_HLEDGE = 2,
      ANIM_HLEDGEOFF = 3,
      ANIM_HLVERT2 = 4,
      ANIM_HLVERT2OFF = 5,
      ANIM_HLEDGE2 = 6,
      ANIM_HLEDGEOFF2 = 7,

      // Test mesh indices:
      TEST_WHEEL = 0,
      TEST_J = 1,
      TEST_PARABOLOID = 2,
      TEST_GRID = 3,

      MCMDSTART = MCONST,
      VERTS = MCONST + 1, // enter 'vertex' state
      FACES = MCONST + 2, // enter 'face' state
      VOFFSET = MCONST + 3, // + n : specify offset to add to each vertex index in face
      CCW = MCONST + 4, // resume counter-clockwise vertex order (default)
      CW = MCONST + 5, // reverse vertex order to clockwise
      WITHZ = MCONST + 6, // declare that vertices include z coordinates (false by default)
      LIFT = MCONST + 7, // + zt zb 'lift' mesh into 3d by duplicating faces and stretching;
      //    zt = z coord for top, zb = z coord for bottom
      SCALE = MCONST + 8, // + f	: scale mesh by factor f / 1000
      SCALEI = MCONST + 9, // + x y z : scale mesh in x,y,z dimensions by x/1000, y/1000,z/1000
      ROTX = MCONST + 10, // + hh rotate around x axis by hex amount hh*0x100
      ROTY = MCONST + 11, // + hh rotate around y axis by hex amount hh*0x100
      ROTZ = MCONST + 12, // + hh rotate around z axis by hex amount hh*0x100
      TL = MCONST + 13, // + x y z : translate mesh by x,y,z
      SETZ = MCONST + 14, // + z : set z-coordinate for subsequent vertices
      ORIGIN = MCONST + 15, // + x y : specify the origin (to subtract) from subsequent vertices
      AUXMAT = MCONST + 16, // apply subsequent matrix operations to auxilliary matrix for child
      CHILD = MCONST + 17, // + n : add child mesh (from library entry #n)
      ENDSCRIPT = MCONST + 18 // end of script
      , COLOR = MCONST + 19 // + n b : specify color index (n) and intensity (b); b=100:normal

      , WHITE = 0
      , LIGHTGRAY = 1
      , GRAY = 2
      , DARKGRAY = 3
      , BLACK = 4
      , RED = 5
      , PINK = 6
      , ORANGE = 7
      , YELLOW = 8
      , GREEN = 9
      , MAGENTA = 10
      , CYAN = 11
      , BLUE = 12
      , BROWN = 13
      , LIGHTBLUE = 14
      , DARKGREEN = 15
      , BLUERED = 16
      , REDGREEN = 17
      , GREENBLUE = 18
      , GRAYRED = 19
      , GRAYGREEN = 20

      , DEFAULT_COLORS = 21
      , COLOR_LEVELS = 64
      , MID_LEVEL = (COLOR_LEVELS / 2)
      , COLOR_RECORDS = 256


      // Color of face, edge, or vertex
      , RS_COLOR = 0
      , RF_COLOR = (COLOR_RECORDS - 1) << RS_COLOR
      , RS_SHADE = RS_COLOR + 8
      , RF_SHADE = (COLOR_LEVELS - 1) << RS_SHADE
      , RS_HIDDEN = RS_SHADE + 6
      , RF_HIDDEN = 0x1 << RS_HIDDEN
      , RS_USER = 30
      , RF_USER = (0x1 << RS_USER)

      // If set, plots in wireframe
      , FS_WIREFRAME = RS_HIDDEN + 1
      , FF_WIREFRAME = (0x1 << FS_WIREFRAME)
      // If set, plots highlight border
      , FS_BORDER = FS_WIREFRAME + 1
      , FF_BORDER = (0x1 << FS_BORDER)
      // If set, sorts by farthest z-coord
      , FS_FARSORT = FS_BORDER + 1
      , FF_FARSORT = (0x1 << FS_FARSORT)
      , FS_NEARSORT = FS_FARSORT + 1
      , FF_NEARSORT = (0x1 << FS_NEARSORT)

      , LS_THICK = RS_HIDDEN + 1
      , LF_THICK = (0x1 << LS_THICK)

      , CS_THICK = LS_THICK
      , CF_THICK = LF_THICK
      ;

}